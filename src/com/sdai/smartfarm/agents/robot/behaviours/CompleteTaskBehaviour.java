package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.List;

import com.sdai.smartfarm.agents.drone.behaviours.RequestAssistanceBehaviour;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.common_behaviours.FollowPathBehaviour;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.logic.AStar;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.models.Task;

import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class CompleteTaskBehaviour extends FollowPathBehaviour {

    private final transient Task task;

    public Task getTask() {
        return task;
    }

    public CompleteTaskBehaviour(RobotAgent agent, long period, Task task) {
        super(agent, period, task.path());

        this.task = task;

        /*List<Position> path = AStar.reachSingleDestination(
            agent.getPosition(), 
            task.request().position(), 
            agent.getObservedEnvironment(), 
            false
        );

        if(Math.abs(path.size() - task.path().size()) > 5) {
            ///System.err.println("expected path buggged");
            this.task = new Task(task.request(), path, task.requester(), task.replyCode());*/
    }

    protected void notifyCompletion() {

        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setConversationId(RequestAssistanceBehaviour.CONVERSATION_ID);
        response.addReceiver(task.requester());
        response.setInReplyTo(task.replyCode());
        response.setContent("Completed the task " + task.request().toString());

        getAgent().send(response);
    }

    protected void notifyFailure() {

        ACLMessage response = new ACLMessage(ACLMessage.FAILURE);
        response.setConversationId(RequestAssistanceBehaviour.CONVERSATION_ID);
        response.addReceiver(task.requester());
        response.setInReplyTo(task.replyCode());
        response.setContent("Failed to complete the task " + task.request().toString());

        getAgent().send(response);
    }

    protected void completeTask() {

        RobotAgent agent = (RobotAgent) getAgent();

        Position position = agent.getPosition();

        CropsNeeds needs = task.request().cropsNeeds();

        if (needs == null)
            return;

        if (needs.getWatering()) {
            agent.getEnvironment().water(position.x(), position.y());

            // While doing this the agent becomes unresponsive which is really not nice, but otherwise we'd 
            // get super agents that can instantly solve any problem
            try {
                Thread.sleep(4 * getPeriod());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new IllegalStateException("got interrupted while sleeping");
            }
        }
        if (needs.getWeedRemoval()) {
            agent.getEnvironment().removeWeeds(position.x(), position.y());

            try {
                Thread.sleep(6 * getPeriod());
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new IllegalStateException("got interrupted while sleeping");
            }
        }

    }

    @Override
    public void stop() {

        RobotAgent agent = (RobotAgent) getAgent();

        Position position = agent.getPosition();

        if (!position.equals(task.request().position())) {
            notifyFailure();

        } else {
            completeTask();

            notifyCompletion();
        }

        if(agent.getTasksSize() == 0) {

            ObservedEnvironment environment = agent.getObservedEnvironment();
            TileType currentTile = environment.map()[agent.getPosition().y() * environment.width() + agent.getPosition().x()];
            if (currentTile == TileType.FARMLAND) {

            }

            // Behaviour moveOutOfFieldsBehaviour = new MoveOutOfFieldsBehaviour(agent, task.path());
            // agent.addBehaviour(moveOutOfFieldsBehaviour);
            // agent.setCurrentState(moveOutOfFieldsBehaviour);

            Behaviour idleBehaviour = new IdleBehaviour();
            agent.addBehaviour(idleBehaviour);
            agent.setCurrentState(idleBehaviour);

        } else {
            Task newTask = agent.pollTask();

            Behaviour newCompleteTaskBehaviour = new CompleteTaskBehaviour(agent, getPeriod(), newTask);
            agent.addBehaviour(newCompleteTaskBehaviour);
            agent.setCurrentState(newCompleteTaskBehaviour);
        }

        super.stop();
        
    }

}
