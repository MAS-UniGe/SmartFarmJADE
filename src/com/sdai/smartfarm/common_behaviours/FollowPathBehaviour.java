package com.sdai.smartfarm.common_behaviours;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.utils.AStar;
import com.sdai.smartfarm.utils.Position;

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

    @Override
    protected void onTick() {

        observeAndUpdate();
        
        if(!pathIterator.hasNext()) {
            stop();
            return;
        }


        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        boolean canFly = (agent instanceof DroneAgent);

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();

        Position currentPosition = agent.getPosition();
        Position nextPosition = pathIterator.next();

        TileType nextTile = observedEnvironment.map()[nextPosition.y() * observedEnvironment.width() + nextPosition.x()];

        boolean encounteredObstacle = false;
        
        while (nextTile == TileType.TALL_OBSTACLE || (nextTile == TileType.OBSTACLE && !canFly)) {
            encounteredObstacle = true;

            if(!pathIterator.hasNext()) {
                stop();
                return;
            }
            nextPosition = pathIterator.next();
            nextTile = observedEnvironment.map()[nextPosition.y() * observedEnvironment.width() + nextPosition.x()];

        }

        if(encounteredObstacle || !currentPosition.isAdjacent(nextPosition)) {
            List<Position> pathToAvoidObstacle = AStar.reachSingleDestination(
                currentPosition, 
                nextPosition, 
                observedEnvironment, 
                canFly
            );

            if (pathToAvoidObstacle == null) { // then path is impossible: this will trigger a failure notification
                stop();
                return;
            }

            //Sadly ListIterator does not have an addAll() method
            for(Position newPathPosition : pathToAvoidObstacle) {
                pathIterator.add(newPathPosition);
            }
            for(int i = 0; i < pathToAvoidObstacle.size(); i++) {
                pathIterator.previous();
            }

            nextPosition = pathIterator.next();
        }

        if(!currentPosition.isAdjacent(nextPosition)) 
            throw new IllegalStateException("There is a bug in the code: positions are not adjacent: " + currentPosition + ", " + nextPosition);
            
        if(currentPosition != nextPosition) {
            
            boolean success = agent.getEnvironment().moveAgent(agent, nextPosition);

            if(success) {
                agent.setPosition(nextPosition);
            } else {
                // can only fail if 2 agents want to go to the same spot at the same exact time and neither of them was occupying it beforehand
                pathIterator.previous(); // we go back one step so that we can retry getting to the same position or avoiding the new obstacle
            }
        }
        
    }
    
}
