package com.sdai.smartfarm.agents.drone.behaviours;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.utils.AssistanceRequest;
import com.sdai.smartfarm.utils.Position;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *  This class is basically a copy in a different domain of the book trading example at 
 *  https://jade.tilab.com/doc/tutorials/JADEProgramming-Tutorial-for-beginners.pdf (p.19)
 *  with some tweaking for fault tolerance
 */
public class RequestAssistanceBehaviour extends Behaviour {

    private static final Logger LOGGER = Logger.getLogger(RequestAssistanceBehaviour.class.getName());

    public static final String CONVERSATION_ID = "assistance-request";

    protected final AssistanceRequest request;
    protected AID bestRobot;
    protected int bestCost;
    protected int repliesExpected;
    protected MessageTemplate mt;
    protected long timeout;
    protected transient List<AID> blacklisted; // going to use a normal list because I expect this data structure to remain SMALL

    protected int step;

    public RequestAssistanceBehaviour(Position position, CropsNeeds cropsNeeds) {
        super();

        this.request = new AssistanceRequest(position, cropsNeeds);
        this.step = 0;
        this.blacklisted = new ArrayList<>(); 
    }

    protected void callForProposal() {

        DroneAgent agent = (DroneAgent) getAgent();

        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        AID[] receivers = agent.getKnown(AgentType.ROBOT);
        
        repliesExpected = 0;
        
        if(receivers != null) {
            repliesExpected = receivers.length;

            for (AID robot : receivers) {
                if (blacklisted.contains(robot)) {
                    repliesExpected--;
                    continue;
                }
                cfp.addReceiver(robot);
            }
        }
        if (repliesExpected == 0) {
            // we either do not know any robot or they are all blacklisted, gonna give up
            step = 4;
            return;
        }
        //LOGGER.info(agent.getLocalName() + ": I'm requesting some help");
        try {
            cfp.setContentObject(request);
            cfp.setConversationId(CONVERSATION_ID);
            cfp.setReplyWith("cfp" + System.currentTimeMillis());
            agent.send(cfp);
        
        } catch(IOException e) {
            LOGGER.severe("assistance request was not serializable...");
            throw new IllegalStateException();
        }

        timeout = System.currentTimeMillis() + 1500;
        mt = MessageTemplate.and(
                MessageTemplate.MatchConversationId(cfp.getConversationId()),
                MessageTemplate.MatchInReplyTo(cfp.getReplyWith())
            );
        step = 1;
    }

    protected void receiveProposals() {

        DroneAgent agent = (DroneAgent) getAgent();

        ACLMessage reply = agent.receive(mt);
        if (reply == null) {
            block();
            return;
        }
        if (reply.getPerformative() == ACLMessage.PROPOSE) {

            LOGGER.fine(agent.getLocalName() + ": received an offer");

            int cost = Integer.parseInt(reply.getContent());
            if (bestRobot == null || cost < bestCost) {
                bestCost = cost;
                bestRobot = reply.getSender();
            }
            
        } else if (reply.getPerformative() == ACLMessage.REFUSE) {

            LOGGER.fine(agent.getLocalName() + ": received a refuse");
            
            String reason = reply.getContent();
            if (reason != null && reason.equals("Already on my way to solve this")) {
                // Then this is a duplicate behaviour (the drone passed twice on the same tile too quickly)
                // and we can discard it
                step = 4;
                return;
            }
        }
        repliesExpected--;
        if (repliesExpected == 0 || System.currentTimeMillis() > timeout) {
            step = 2;
        }
    }

    protected void acceptProposal() {

        DroneAgent agent = (DroneAgent) getAgent();

        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        order.addReceiver(bestRobot);
        try {
            order.setContentObject(request);
        } catch(IOException e) {
            LOGGER.severe("assistance request was not serializable...");
            throw new IllegalStateException();
        }
        order.setConversationId(CONVERSATION_ID);
        order.setReplyWith("order"+System.currentTimeMillis());
        mt = MessageTemplate.and(
            MessageTemplate.MatchConversationId(order.getConversationId()),
            MessageTemplate.MatchInReplyTo(order.getReplyWith())
        );

        agent.send(order);
        step = 3;
    }

    protected void receiveResult() {

        DroneAgent agent = (DroneAgent) getAgent();

        ACLMessage taskCompletionReply = agent.receive(mt);
        if (taskCompletionReply == null) {
            block();
            return;
        }
        if (taskCompletionReply.getPerformative() == ACLMessage.INFORM) {
            // Success
            LOGGER.info("successfully helped crops at: " + request.position());
            step = 4;
        } else if (taskCompletionReply.getPerformative() == ACLMessage.FAILURE) {
            // Robot failed, gonna ask another one
            blacklisted.add(taskCompletionReply.getSender());
            bestRobot = null;
            step = 0;
        }
    }

    @Override
    public void action() {

        switch(step) {

            case 0:
                callForProposal();
                break;

            case 1:
                receiveProposals();
                break;

            case 2:
                acceptProposal();
                break;

            case 3:
                receiveResult();
                break;
        }
    }

    @Override
    public boolean done() {
        return ((step == 2 && bestRobot == null) || step == 4);
    }


    
}
