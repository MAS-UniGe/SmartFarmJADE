package com.sdai.smartfarm.agents.tractor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.agents.tractor.behaviours.TractorInitBehaviour;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.logic.Distributions;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.settings.CropsSettings;

public class TractorAgent extends BaseFarmingAgent {

    private final transient AgentsSettings agentsSettings = AgentsSettings.defaultAgentsSettings();

    private final transient CropsSettings cropsSettings = CropsSettings.defaultCropsSettings();

    /* STATIC STUFF */

    private static final Logger LOGGER = Logger.getLogger(RobotAgent.class.getName());

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return TractorAgent.instanceCounter++;
    }

    //////////////////

    // I really wish I was able to expain this, but I really cannot put it into words
    // The only thing I can say is: I need a load balancer also for tractors to avoid them all store copies of the same huge
    // prediction map when they would only care about the tiles in their assigned fields
    // BTW I cannot use an array here because of type erasure
    protected transient List<Deque<Double>> rewardsPredictionsMap;

    protected boolean evaluationScheduled = false;

    public boolean isEvaluationScheduled() {
        return evaluationScheduled;
    }

    public void setEvaluationScheduled(boolean evaluationScheduled) {
        this.evaluationScheduled = evaluationScheduled;
    }

    public List<Deque<Double>> getRewardsPredictionsMap() {
        return rewardsPredictionsMap;
    }

    public void setRewardsPredictionsMap(List<Deque<Double>> rewardsPredictionsMap) {
        this.rewardsPredictionsMap = rewardsPredictionsMap;
    }

    @Override
    public AgentType getType() {
        return AgentType.TRACTOR;
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null || args.length < 1) 
            throw new IllegalArgumentException("setup: not enough arguments");
        
        Environment environment = (Environment) args[0];

        Integer x = null;
        Integer y = null;

        if (args.length >= 3) {
            x = (int) args[1];
            y = (int) args[2];
        }

        situate(environment, x, y);

        registerToYellowPages();

        addBehaviour(new TractorInitBehaviour());
        
    }   

    public Deque<Double> computeRewardPredictionForCrop(CropsState state, double growth, double wellBeing) {

        Deque<Double> predictions = new ArrayDeque<>();
        List<double[]> wellBeingDistributions;
        List<double[]> growthDistributions;

        switch(state) {

            case HEALTHY:
                wellBeingDistributions = Distributions.healthyWellBeingDistributions;
                growthDistributions = Distributions.healthyGrowthDistributions;
                break;

            case UNWELL:
                wellBeingDistributions = Distributions.unwellWellBeingDistributions;
                growthDistributions = Distributions.unwellGrowthDistributions;
                break;

            default: // case DEAD
                for(int t = 0; t < agentsSettings.rewardPredictionSize(); t++) {
                    predictions.addLast(0.0);
                }
                return predictions;
        }

        if (state == CropsState.DEAD) {

            for(int t = 0; t < agentsSettings.rewardPredictionSize(); t++) {
                predictions.addLast(0.0);
            }
            
            return predictions;
        }
        
        double pAlive = 1.0;

        for(int t = 0; t < agentsSettings.rewardPredictionSize(); t++) {
            
            // get probability of being in decay (wellBeing becomes < wellBeingThreshold)
            double pDecay;
            int wellBeingOffset = (int) ((cropsSettings.wellBeingThreshold() + (1.0 - wellBeing)) * (agentsSettings.statesBinsAmount() - 1));
            if (wellBeingOffset >= agentsSettings.statesBinsAmount()) {
                pDecay = 1.0;
            } else {
                pDecay = wellBeingDistributions.get(t)[wellBeingOffset];
            }

            pAlive *= 1.0 - pDecay * cropsSettings.dyingChance();

            // get probability of being harvestable (growth >= 1.0)
            double pHarvestable;
            int growthOffset = (int) ((1.0 - growth) * (agentsSettings.statesBinsAmount() - 1));
            if (growthOffset < 0) {
                pHarvestable = 1.0;
            } else {
                pHarvestable = growthDistributions.get(t)[growthOffset];
            }

            predictions.addLast(pHarvestable * pAlive);

        }

        return predictions;

    }
    
}
