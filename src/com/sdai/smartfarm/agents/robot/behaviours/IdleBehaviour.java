package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.logging.Logger;

import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.models.Task;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.behaviours.Behaviour;

public class IdleBehaviour extends Behaviour {

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    private static final Logger LOGGER = Logger.getLogger(IdleBehaviour.class.getName());

    private boolean done = false;

    @Override
    public void action() {
        
        RobotAgent agent = (RobotAgent) getAgent();

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
