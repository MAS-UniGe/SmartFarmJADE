package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.drone.behaviours.RequestAssistanceBehaviour;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.logic.AStar;
import com.sdai.smartfarm.models.AssistanceRequest;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.models.Task;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class AcceptTasksBehaviour extends CyclicBehaviour {
    
    private static final Logger LOGGER = Logger.getLogger(AcceptTasksBehaviour.class.getName());

    @Override
    public void action() {
        RobotAgent agent = (RobotAgent) getAgent();

        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
            MessageTemplate.MatchConversationId(RequestAssistanceBehaviour.CONVERSATION_ID)
        );

        ACLMessage msg = agent.receive(mt);

        if(msg == null) {
            block();
            return;
        }

        String replyCode = msg.getReplyWith();

        try {
            
            AssistanceRequest request = (AssistanceRequest) msg.getContentObject();

            Position lastPosition = agent.getLastTaskDestination();

            List<Position> path = AStar.reachSingleDestination(
                lastPosition, 
                request.position(), 
                agent.getObservedEnvironment(), 
                agent.getType(),
                agent.getFieldsMap(),
                null
            );

            if (path==null) {
                ACLMessage response = new ACLMessage(ACLMessage.FAILURE);
                response.setConversationId(RequestAssistanceBehaviour.CONVERSATION_ID);
                response.addReceiver(msg.getSender());
                response.setInReplyTo(replyCode);
                response.setContent("Impossible to complete the task " + request.toString());

                getAgent().send(response);
                return;
            }

            agent.assignTask(new Task(request, path, msg.getSender(), replyCode));
            if(agent.getCurrentState() instanceof IdleBehaviour idle) {
                idle.restart();
            }

        } catch(UnreadableException | ClassCastException e) {
            LOGGER.warning("got an unreadable assignment, ignoring...");
        }

    }
    
}
