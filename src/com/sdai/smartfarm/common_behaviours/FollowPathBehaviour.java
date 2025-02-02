package com.sdai.smartfarm.common_behaviours;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.utils.AStar;
import com.sdai.smartfarm.utils.Position;

import jade.core.behaviours.TickerBehaviour;

public class FollowPathBehaviour extends TickerBehaviour {

    private static final Logger LOGGER = Logger.getLogger(FollowPathBehaviour.class.getName());

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

    @Override
    protected void onTick() {
        
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

            if (pathToAvoidObstacle == null) { // then path is impossible
                // TODO: find a recovery plan for this thing
                throw new IllegalStateException("need a recovery plan for when path is impossible");
            }

            pathToAvoidObstacle.add(nextPosition); // gotta re-add it at the end

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
            
            boolean success = agent.getEnvironment().moveAgent(currentPosition.x(), currentPosition.y(), nextPosition.x(), nextPosition.y());

            if(success) {
                agent.setPosition(nextPosition);
            } else {
                // can only fail if 2 agents want to go to the same spot at the same exact time and neither of them was occupying it beforehand
                pathIterator.previous(); // we go back one step so that we can retry getting to the same position or avoiding the new obstacle
            }
        }
        
    }
    
}
