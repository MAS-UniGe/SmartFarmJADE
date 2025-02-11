package com.sdai.smartfarm.agents.drone.behaviours;

import java.io.IOException;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.common_behaviours.FollowPathBehaviour;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.models.CropsGrowthMeasurement;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class CheckOnCropsBehaviour extends FollowPathBehaviour {

    private static final Logger LOGGER = Logger.getLogger(CheckOnCropsBehaviour.class.getName());

    protected transient AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    public CheckOnCropsBehaviour(DroneAgent agent, long period) {
        
        super(agent, period, agent.getPathPlan());

    }

    protected void notifyTractors(Observation observation) {

        DroneAgent agent = (DroneAgent) getAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID[] receivers = agent.getKnown(AgentType.TRACTOR);

        if(receivers != null) {
            for (AID tractor : receivers) {
                msg.addReceiver(tractor);
            }
        }
        msg.setConversationId("Crops-Growth");
        try {
            msg.setContentObject(new CropsGrowthMeasurement(
                agent.getPosition(),
                observation.growth(),
                observation.wellBeing(),
                observation.cropsState()
            ));
        } catch(IOException e) {
            LOGGER.severe("Growth Update was not serializable");
            throw new IllegalStateException(e.getMessage());
        }

        agent.send(msg);
        
    }

    @Override
    protected Observation observeAndUpdate() {

        Observation observation = super.observeAndUpdate();

        DroneAgent agent = (DroneAgent) getAgent();

        CropsState cropsState = observation.cropsState();
        CropsNeeds needs = observation.needs();

        if(observation.cropsState() != null) {
            // then its a farmland tile
            notifyTractors(observation);
        }

        if(cropsState == CropsState.UNWELL) {
            // this needs a separate behaviour because it involves a conversation and decision making
            agent.addBehaviour(new RequestAssistanceBehaviour(agent.getPosition(), needs));
        }
        
        return observation;
    }
    
    @Override
    public void stop() {
        DroneAgent agent = (DroneAgent) getAgent();

        agent.addBehaviour(new CheckOnCropsBehaviour(agent, this.getPeriod()));

        super.stop();
    }

}
