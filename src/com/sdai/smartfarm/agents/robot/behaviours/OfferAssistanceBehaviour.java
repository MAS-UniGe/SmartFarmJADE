package com.sdai.smartfarm.agents.robot.behaviours;

import java.util.logging.Logger;

import com.sdai.smartfarm.agents.drone.behaviours.RequestAssistanceBehaviour;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.models.AssistanceRequest;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class OfferAssistanceBehaviour extends CyclicBehaviour {

    private static final Logger LOGGER = Logger.getLogger(OfferAssistanceBehaviour.class.getName());

    @Override
    public void action() {
        RobotAgent agent = (RobotAgent) getAgent();

        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.CFP),
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

            if (agent.isHandlingRequest(request)) {
                ACLMessage response = new ACLMessage(ACLMessage.REFUSE);
                response.setConversationId(RequestAssistanceBehaviour.CONVERSATION_ID);
                response.setInReplyTo(replyCode);
                response.setContent("Already on my way to solve this");

                agent.send(response);
                return;
            }
            // Else it does not and it must compute the cost of handling a further request

            int cost = agent.estimateCostForRequest(request);
            ACLMessage response = new ACLMessage(ACLMessage.PROPOSE);
            response.setConversationId(RequestAssistanceBehaviour.CONVERSATION_ID);
            response.setInReplyTo(replyCode);
            response.setContent(String.valueOf(cost));
            response.addReceiver(msg.getSender());
            agent.send(response);

        } catch(UnreadableException | ClassCastException e) {
            LOGGER.warning("got an unreadable assignment, ignoring...");
        }

    }
    
}
