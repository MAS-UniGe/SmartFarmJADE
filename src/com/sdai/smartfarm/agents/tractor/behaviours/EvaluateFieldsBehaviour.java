package com.sdai.smartfarm.agents.tractor.behaviours;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.models.FarmField;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class EvaluateFieldsBehaviour extends OneShotBehaviour {

    private static final Logger LOGGER = Logger.getLogger(EvaluateFieldsBehaviour.class.getName());

    private static final AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    protected void notifyAgents(int fieldId) {

        TractorAgent agent = (TractorAgent) getAgent();

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setConversationId("Field-Harvest");
        
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

        agent.send(msg);
    }

    @Override
    public void action() {
        
        TractorAgent agent = (TractorAgent) getAgent();

        int fieldId = 0;

        for (FarmField field : agent.getFields()) {

            if(!agent.getFieldsToAvoid().contains(fieldId)) {// then the field is already being harvested
                fieldId++;
                continue;
            }

            Deque<Double> totalRewardPrediction = field.totalRewardPrediction();

            Iterator<Double> iterator = totalRewardPrediction.iterator();

            double maxReward = 0.0;
            int maxRewardPosition = 0;

            int index = 0;

            while(iterator.hasNext()) {
                
                Double reward = iterator.next();
                if (reward != null && reward > maxReward) {
                    maxRewardPosition = index;
                    maxReward = reward;
                }

                index++;
            }

            double rewardPercentage = maxReward / totalRewardPrediction.size();

            // we schedule an harvest only if we're at the right moment
            if (maxRewardPosition == 0 && rewardPercentage > settings.minimumRewardPercentage()) {

                agent.allowField(fieldId);

                if(agent.getHarvestSize() == 0)
                    agent.addBehaviour(new HarvestCropsBehaviour(agent, (long) (1000 / settings.tractorSpeed()), fieldId));

                agent.assignFieldForHarvest(fieldId);

                notifyAgents(fieldId);
            }

            fieldId++;

        }

        agent.setEvaluationScheduled(false);

    }
    
}
