package com.sdai.smartfarm.behaviours;

import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.DroneAgent;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class DroneConsumerBehaviour extends CyclicBehaviour {

    private static final Logger logger = Logger.getLogger(DroneConsumerBehaviour.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void action() {

        DroneAgent agent = (DroneAgent) getAgent();

        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchOntology("Region-Assignment")
        );
        
        ACLMessage msg = myAgent.receive();

        if (msg == null) {
            block();
            return;
        }

        logger.info(agent.getLocalName() + ": received a message");
         
        try {
            List<int[]> assignedRegion = (List<int[]>) msg.getContentObject();
            agent.setAssignedTiles(assignedRegion);

        } catch(UnreadableException | ClassCastException e) {
            logger.warning("got an unreadable assignment, ignoring...");
        }
        

    }
    
}
