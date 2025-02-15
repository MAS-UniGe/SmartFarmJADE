package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.models.Task;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.behaviours.Behaviour;

public class IdleBehaviour extends Behaviour {

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    private static final Logger LOGGER = Logger.getLogger(IdleBehaviour.class.getName());

    private boolean done = false;

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
    public void action() {
        
        RobotAgent agent = (RobotAgent) getAgent();

        observeAndUpdate();

        if(agent.getTasksSize() == 0) {
            block();
            return;
        } 

        Task newTask = agent.pollTask();

        Behaviour completeTaskBehaviour = new CompleteTaskBehaviour(agent, (long) (1000.0 / settings.robotSpeed()), newTask);

        agent.addBehaviour(completeTaskBehaviour);
        agent.setCurrentState(completeTaskBehaviour);

        done = true;
        LOGGER.fine(getAgent().getLocalName() + ": has stopped being Idle");
        
    }

    @Override
    public boolean done() {
        return done;
    }


}
