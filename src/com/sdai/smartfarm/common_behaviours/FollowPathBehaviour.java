package com.sdai.smartfarm.common_behaviours;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.logic.AStar;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.behaviours.TickerBehaviour;

public class FollowPathBehaviour extends TickerBehaviour {

    private static final Logger LOGGER = Logger.getLogger(FollowPathBehaviour.class.getName());

    protected transient AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    protected final LinkedList<Position> path;

    protected final transient ListIterator<Position> pathIterator;

    public FollowPathBehaviour(BaseFarmingAgent agent, long period, List<Position> path) {
        super(agent, period);

        if(path == null) {
            this.path = null;
            this.pathIterator = Collections.emptyListIterator();
            return;
        }

        this.path = new LinkedList<>(path);
        
        this.pathIterator = this.path.listIterator();
        
    }

    
    protected Observation observeAndUpdate() {

        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        Position currentPosition = agent.getPosition();

        Observation observation = agent.getEnvironment().observe(currentPosition.x(), currentPosition.y(), settings.viewRange());

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();

        int viewSize = 2 * settings.viewRange() + 1;

        for (int y = 0; y < viewSize; y++) {
            for (int x = 0; x < viewSize; x++) {

                TileType observedTile = observation.tiles()[y * viewSize + x];
                AgentType observedAgent = observation.agents()[y * viewSize + x];

                int mapY = currentPosition.y() + y - settings.viewRange();
                int mapX = currentPosition.x() + x - settings.viewRange();

                if (observedTile != null) {
                    observedEnvironment.map()[mapY * observedEnvironment.width() + mapX] = observedTile;
                }

                if (observedAgent != null && (mapX != currentPosition.x() || mapY != currentPosition.y())) {
                    // Yes, we consider other agents obstacles
                    observedEnvironment.map()[mapY * observedEnvironment.width() + mapX] = TileType.TALL_OBSTACLE;
                }
                
            }
        }

        return observation;
    }

    protected boolean canMoveThere(
        BaseFarmingAgent agent,
        Position nextPosition
    ) {
        ObservedEnvironment environment = agent.getObservedEnvironment();

        // Can't go out of bounds
        if (
            nextPosition.x() < 0 
            || nextPosition.x() >= environment.width() 
            || nextPosition.y() < 0 
            || nextPosition.y() >= environment.height()
        ) 
            return false;

        int index1 = nextPosition.y() * environment.width() + nextPosition.x();

        TileType nextPosTile = environment.map()[index1];

        // Differently from the planning part, for this check we don't care about robots vertical movement

        // Cannot avoid tall obstacles and cannot fly over short ones if not drone
        if (
            nextPosTile == TileType.TALL_OBSTACLE
            || (nextPosTile == TileType.OBSTACLE && agent.getType() != AgentType.DRONE)
        )
            return false;

        int nextField = agent.getFieldsMap()[index1];

        return (agent.getFieldsToAvoid().contains(nextField));

    }

    @Override
    protected void onTick() {

        observeAndUpdate();
        
        if(!pathIterator.hasNext()) {
            stop();
            return;
        }


        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();

        Position currentPosition = agent.getPosition();
        Position nextPosition = pathIterator.next();
        
        while (!canMoveThere(agent, nextPosition)) {

            if(!pathIterator.hasNext()) {
                stop();
                return;
            }
            nextPosition = pathIterator.next();

        }

        if(!currentPosition.isAdjacent(nextPosition)) {
            List<Position> pathToAvoidObstacle = AStar.reachSingleDestination(
                currentPosition, 
                nextPosition, 
                observedEnvironment, 
                agent.getType(),
                agent.getFieldsMap(),
                agent.getFieldsToAvoid()
            );

            if (pathToAvoidObstacle == null) { // then path is impossible: this will trigger a failure notification
                stop();
                return;
            }

            // Sadly ListIterator does not have an addAll() method
            for(Position newPathPosition : pathToAvoidObstacle) {
                pathIterator.add(newPathPosition);
            }
            for(int i = 0; i < pathToAvoidObstacle.size(); i++) {
            // And there is no way to clone an iterator, so we must go back manually :/
                pathIterator.previous();
            }

            nextPosition = pathIterator.next();
        }

        if(!currentPosition.isAdjacent(nextPosition)) {
            LOGGER.severe("There is a bug in the code: positions are not adjacent: " + currentPosition + ", " + nextPosition);
            throw new IllegalStateException("positions are not adjacent");
        }
            
        if(currentPosition != nextPosition) {
            
            boolean success = agent.getEnvironment().moveAgent(agent, nextPosition);

            if(success) {
                agent.setPosition(nextPosition);

            } else {
                // can only fail if 2 agents want to go to the same spot at the same exact time and neither of them was occupying it beforehand
                pathIterator.previous();  // we go back one step so that we can retry getting to the same position or avoiding the new obstacle

            }
        }
        
    }
    
}
