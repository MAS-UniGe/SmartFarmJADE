package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.utils.AssistanceRequest;
import com.sdai.smartfarm.utils.Position;
import com.sdai.smartfarm.utils.Task;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;


public class MoveOutOfFieldsBehaviour extends OneShotBehaviour {

    private static final Logger LOGGER = Logger.getLogger(MoveOutOfFieldsBehaviour.class.getName());

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    protected final transient List<Position> lastPath;

    public MoveOutOfFieldsBehaviour(RobotAgent agent, List<Position> lastPath) {
        super(agent);

        this.lastPath = lastPath;
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

        //if(tile == TileType.PATH) {

            Behaviour idleBehaviour = new IdleBehaviour();
            agent.addBehaviour(idleBehaviour);
            agent.setCurrentState(idleBehaviour);

            return;
        //} 

/* 
        // in this case we just follow the last path in reverse until we're out of the field
        List<Position> pathToGetOutOfFiels = new LinkedList<>();

        ListIterator<Position> iterator = lastPath.listIterator(lastPath.size());

        Position nextPosition = agent.getPosition();

        while(iterator.hasPrevious()) {
            nextPosition = iterator.previous();
            pathToGetOutOfFiels.add(nextPosition);

            tile = environment.map()[nextPosition.y() * environment.width() + nextPosition.x()];
            if (tile == TileType.PATH) {
                break;
            }
        }

        Task moveOutOfFieldsTask = new Task(
            new AssistanceRequest(nextPosition, null),  // Fake assistance request
            pathToGetOutOfFiels, 
            agent.getAID(), 
            null
        );

        Behaviour completeTaskBehaviour = new CompleteTaskBehaviour(agent, (long) (1000.0 / settings.robotSpeed()), moveOutOfFieldsTask);
        agent.addBehaviour(completeTaskBehaviour);
        agent.setCurrentState(completeTaskBehaviour);*/
        
    }
    
}
