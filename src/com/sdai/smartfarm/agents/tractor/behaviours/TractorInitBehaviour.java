package com.sdai.smartfarm.agents.tractor.behaviours;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.common_behaviours.InitBehaviour;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.logic.VectorUtils;
import com.sdai.smartfarm.models.FarmField;
import com.sdai.smartfarm.models.Position;

public class TractorInitBehaviour extends InitBehaviour {
    
    @Override
    protected void addBehaviours(BaseFarmingAgent agent) {
        
        // Yes, I want a ClassCastException here if you give something called "TractorInitBehaviour" to anything but a Tractor
        if (!(agent instanceof TractorAgent tractorAgent)) throw new ClassCastException("TractorInitBehaviour should be given only to a Tractor!");

        super.addBehaviours(tractorAgent);
        agent.addBehaviour(new ReceiveMeasurementBehaviour());

    }


    @Override
    public void action() {
        super.action();

        TractorAgent agent = (TractorAgent) getAgent();

        //Set fields to avoid to every field
        for(int i = 0; i < agent.getFields().size(); i++) {
            agent.avoidField(i);
        }
 
        // This is most likely going to be an array of zeros, unless you set the 
        // rewardPredictionSize big enough to contain some possible reward towards the
        // latest timesteps
        Deque<Double> initialRewardPrediction = agent.computeRewardPredictionForCrop(CropsState.HEALTHY, 0.0, 1.0);

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();
        
        width = observedEnvironment.width();
        height = observedEnvironment.height();

        ArrayList<Deque<Double>> rewardsPredictionsMap = new ArrayList<>(Collections.nCopies(width * height, null));

        for(FarmField field : fields) {

            for (int t = 0; t < agentsSettings.rewardPredictionSize(); t++) {
                field.totalRewardPrediction().addLast(0.0);
            }

            for(int i = 0; i < field.size(); i++) {
                Position position = field.positions().get(i);

                int index = position.y() * width + position.x();
                rewardsPredictionsMap.set(index, new ArrayDeque<>(initialRewardPrediction));
                VectorUtils.sumInPlace(field.totalRewardPrediction(), initialRewardPrediction);
            }

        }

        agent.setRewardsPredictionsMap(rewardsPredictionsMap);
    }

}
