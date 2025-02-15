package com.sdai.smartfarm.agents.tractor.behaviours;

import java.util.Deque;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.models.CropsGrowthMeasurement;
import com.sdai.smartfarm.utils.VectorUtils;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class ReceiveMeasurementBehaviour extends CyclicBehaviour {

    private static final Logger LOGGER = Logger.getLogger(ReceiveMeasurementBehaviour.class.getName());

    protected void incorporateMeasurement(CropsGrowthMeasurement measurement) {

        TractorAgent agent = (TractorAgent) getAgent();

        ObservedEnvironment environment = agent.getObservedEnvironment();

        int index = environment.width() * measurement.position().y() + measurement.position().x();

        int field = agent.getFieldsMap()[index];

        // compute updated prediction
        Deque<Double> updatedPrediction = agent.computeRewardPredictionForCrop(measurement.state(), measurement.growth(), measurement.wellBeing());

        // get a reference to the previous prediction
        Deque<Double> previousPrediction = agent.getRewardsPredictionsMap().get(index);

        // replace previous prediciton
        agent.getRewardsPredictionsMap().set(index, updatedPrediction);

        // update total reward prediction by subtracting the previous one and adding the new one
        VectorUtils.sumAndSubtractInPlace(agent.getFields().get(field).totalRewardPrediction(), updatedPrediction, previousPrediction);

    }

    @Override
    public void action() {
        
        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchConversationId("Crops-Growth")
        );

        ACLMessage msg = getAgent().receive(mt);

        if(msg == null) {
            block();
            return;
        }

        try {
            //LOGGER.info("handling message");
            CropsGrowthMeasurement measurement = (CropsGrowthMeasurement) msg.getContentObject();
            incorporateMeasurement(measurement);
            
        } catch(UnreadableException | ClassCastException e) {
            LOGGER.warning("got an unreadable measurement, ignoring...");
        }

    }
    
}
