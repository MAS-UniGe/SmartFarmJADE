package com.sdai.smartfarm.agents.tractor.behaviours;

import java.util.Deque;
import java.util.List;

import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.models.FarmField;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.settings.SimulationSettings;

import jade.core.behaviours.TickerBehaviour;

public class UpdatePredictionsBehaviour extends TickerBehaviour {

    private static final SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();

    protected final double realClockToTickRatio;

    protected double counter;

    public UpdatePredictionsBehaviour(TractorAgent a, long period) {
        super(a, period);

        double realClockUPS = simulationSettings.targetUPS();

        realClockToTickRatio = realClockUPS * period / 1000.0;

        counter = 0.0;
    }


    // a real update only comes in when new information is available,
    // in this function we limit to have the deque shift one
    // position to the left to emulate te advancing of time
    protected void updatePredictions(TractorAgent agent) {
        
        List<Deque<Double>> predictions = agent.getRewardsPredictionsMap();

        int width = agent.getObservedEnvironment().width();

        for (FarmField field : agent.getFields()) {
            for (Position position : field.positions()) {

                int index = width * position.y() + position.x();
                Deque<Double> prediction = predictions.get(index);

                prediction.poll();
                prediction.addLast(null); // I add null and not 0.0 because I want to make clear this states for "I don't know" (even though it behaves the same)

            }

            field.totalRewardPrediction().poll();
            field.totalRewardPrediction().addLast(null);
        }

    }


    @Override
    protected void onTick() {

        TractorAgent agent = (TractorAgent) getAgent();

        counter += realClockToTickRatio;

        while(counter > 1.0) {
            counter -= 1.0;
            updatePredictions(agent);
            
            if (!agent.isEvaluationScheduled()) {
                agent.addBehaviour(new EvaluateFieldsBehaviour());
                agent.setEvaluationScheduled(true);

            }

        }

    }

    
    
}
