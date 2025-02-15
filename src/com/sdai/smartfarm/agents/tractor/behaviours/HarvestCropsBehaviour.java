package com.sdai.smartfarm.agents.tractor.behaviours;

import java.io.IOException;
import java.util.logging.Logger;

import com.sdai.smartfarm.Main;
import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.common_behaviours.FollowPathBehaviour;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.models.Position;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class HarvestCropsBehaviour extends FollowPathBehaviour {

    private static final Logger LOGGER = Logger.getLogger(HarvestCropsBehaviour.class.getName());

    private double reward = 0.0;

    private final int fieldId;

    public HarvestCropsBehaviour(TractorAgent agent, long period, int fieldId) {
        
        // I should use an algorithm similar to the drone's path planner. But this works fine for now
        super(agent, period, agent.getFields().get(fieldId).positions());

        this.fieldId = fieldId;
    }

    protected void notifyAgents() {

        TractorAgent agent = (TractorAgent) getAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM); // could be a request but I want to use this convId also for the completion notification
        msg.setConversationId("Field-Harvest");
        msg.setContent("allow");

        for(AID receiver: agent.getKnown(AgentType.DRONE)) {
            msg.addReceiver(receiver);
        }
        for(AID receiver: agent.getKnown(AgentType.ROBOT)) {
            msg.addReceiver(receiver);
        }

        try {
            msg.setContentObject(fieldId);
        } catch (IOException e) {
            
            LOGGER.severe("fieldId was not serializable");
            throw new IllegalStateException(e.getMessage());
        }

        getAgent().send(msg);
    }
    
    @Override
    public void onTick() {
        super.onTick();

        TractorAgent agent = (TractorAgent) getAgent();

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();
        
        ObservableEnvironment environment = agent.getEnvironment();

        Position position = agent.getPosition();

        int index = position.y() * observedEnvironment.width() + position.x();

        if (observedEnvironment.map()[index] == TileType.FARMLAND && agent.getFieldsMap()[index] == fieldId) {
            reward += environment.harvest(position.x(), position.y());
        }

    }

    @Override
    public void stop() {

        // Normally I'd should send this info to somebody, but in this simulation I limit to add it to a global counter
        Main.addTotalReward(reward);

        LOGGER.info("TOTAL REWARD: " + Main.getTotalReward());

        notifyAgents();

        TractorAgent agent = (TractorAgent) getAgent();

        agent.completeHarvest();

        if(agent.getHarvestSize() > 0) {
            int nextFieldId = agent.getNextFieldToHarvest();
            
            agent.addBehaviour(new HarvestCropsBehaviour(agent, getPeriod(), nextFieldId));
        }

        super.stop();
    }

}
