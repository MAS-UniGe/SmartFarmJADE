package com.sdai.smartfarm.common_behaviours;

import java.util.logging.Logger;

import com.sdai.smartfarm.agents.BaseFarmingAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveHarvestNotificationBehaviour extends CyclicBehaviour {

    private static final Logger LOGGER = Logger.getLogger(ReceiveHarvestNotificationBehaviour.class.getName());

    @Override
    public void action() {
        
        MessageTemplate mt = MessageTemplate.MatchConversationId("Field-Harvest");

        ACLMessage msg = getAgent().receive(mt);

        if(msg == null) {
            block();
            return;
        }

        try {

            int fieldId = (int) msg.getContentObject();
            
            BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

            if(msg.getPerformative() == ACLMessage.REQUEST)
                agent.avoidField(fieldId);
            else 
                agent.allowField(fieldId);
            
        } catch(UnreadableException | ClassCastException e) {
            LOGGER.warning("got an unreadable measurement, ignoring...");
        }

    }
    
}
