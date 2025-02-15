package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.models.AssistanceRequest;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.models.Task;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.utils.AStar;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

/* NOT IMPLEMENTED IN TIME :(

public class MoveOutOfFieldsBehaviour extends OneShotBehaviour {

    private static final Logger LOGGER = Logger.getLogger(MoveOutOfFieldsBehaviour.class.getName());

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    private final int fieldId;

    public MoveOutOfFieldsBehaviour(RobotAgent agent, int fieldId) {
        super(agent);

        this.fieldId = fieldId;
    }

    @Override
    public void action() {
        
        RobotAgent agent = (RobotAgent) getAgent();

        if (agent.getTasksSize() > 0) {
            // This check may seem redundant but we must remember that between the done() of the CompleteTaskBehaviour and this action() anything could happen

            Task newTask = agent.pollTask();

            Behaviour newCompleteTaskBehaviour = new CompleteTaskBehaviour(agent, (long)(1000.0 / settings.robotSpeed()), newTask);
            agent.addBehaviour(newCompleteTaskBehaviour);
            agent.setCurrentState(newCompleteTaskBehaviour);

            return;
        }

        ObservedEnvironment environment = agent.getObservedEnvironment();

        TileType tile = environment.map()[agent.getPosition().y() * environment.width() + agent.getPosition().x()];

        int currentFieldId = agent.getFieldsMap()[];

        if(currentFieldId != fieldId) {

            Behaviour idleBehaviour = new IdleBehaviour();
            agent.addBehaviour(idleBehaviour);
            agent.setCurrentState(idleBehaviour);

            return;
        } 

 
        // in this case we just follow the last path in reverse until we're out of the field
        // TODO: I need something better as this doesn't work if 2 tiles next to each other are fixed one after the other
        Random rngTileSelector = new Random();
        int destFieldId = fieldId;

        // MONTE CARLO
        while(destFieldId == fieldId) {
            int randomX = rngTileSelector.nextInt(0, environment.width() - 1);
            int randomY = rngTileSelector.nextInt(0, environment.height() - 1);
    
            destFieldId = agent.getFieldsMap()[];
        }

        List<Position> path = AStar.reachSingleDestination();
        
        if (path == null) {
            // schedule this behaviour once again
        }

        Task moveOutOfFieldsTask = new Task(
            new AssistanceRequest(nextPosition, null),  // Fake assistance request
            path, 
            agent.getAID(), 
            null
        );

        Behaviour completeTaskBehaviour = new CompleteTaskBehaviour(agent, (long) (1000.0 / settings.robotSpeed()), moveOutOfFieldsTask);
        agent.addBehaviour(completeTaskBehaviour);
        agent.setCurrentState(completeTaskBehaviour);
        
    }
    
}
*/