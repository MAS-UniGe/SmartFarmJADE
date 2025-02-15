package com.sdai.smartfarm.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.settings.CropsSettings;

public class Distributions {

    private static CropsSettings cropsSettings = CropsSettings.defaultCropsSettings();

    private static AgentsSettings agentsSettings = AgentsSettings.defaultAgentsSettings();

    public static final List<double[]> healthyWellBeingDistributions = new ArrayList<>();
    public static final List<double[]> healthyGrowthDistributions = new ArrayList<>();

    public static final List<double[]> unwellWellBeingDistributions = new ArrayList<>();
    public static final List<double[]> unwellGrowthDistributions = new ArrayList<>();

    private Distributions() {}

    private static void initDistributions(
        int distrSize,
        double[] growthDistribution,
        double[] wellBeingDistribution,
        double[] healthyGrowthIncreaseDistribution,
        double[] unwellGrowthIncreaseDistribution,
        double[] wellBeingDecreaseDistribution
    ) {

        // set probability distributions at t0
        // both these distributions are for values between 0 and 1 (out of range values are clamped)
        // index of 0.0 = 0
        // index of 1.0 = size - 1
        makeImpulse(growthDistribution, 0, 1.0);
        makeImpulse(wellBeingDistribution, distrSize - 1, 1.0);

        // set growth rate distributions for healthy and unwell states
        int worstGrowthIndex = (int) (0.5 * distrSize * cropsSettings.healthyGrowthRate());
        int bestGrowthIndex = (int) (distrSize * cropsSettings.healthyGrowthRate());
        makeUniformDistribution(healthyGrowthIncreaseDistribution, worstGrowthIndex, bestGrowthIndex);

        worstGrowthIndex = (int) (0.5 * distrSize * cropsSettings.unwellGrowthRate());
        bestGrowthIndex = (int) (distrSize * cropsSettings.unwellGrowthRate());
        makeUniformDistribution(unwellGrowthIncreaseDistribution, worstGrowthIndex, bestGrowthIndex);

        // this one instead goes from -1.0 to 0.0:
        // it represents the decrease in wellBeing probability distribution
        int worstDecreaseIndex = (int) (distrSize * (1.0 - cropsSettings.wellBeingDecrease()));
        int bestDecreaseIndex = (int) (distrSize * (1.0 - 0.5 * cropsSettings.wellBeingDecrease()));
        makeUniformDistribution(wellBeingDecreaseDistribution, worstDecreaseIndex, bestDecreaseIndex);

    }

    public static void makeImpulse(double[] distribution, int position, double intensity) {

        Arrays.fill(distribution, 0.0);
        distribution[position] = intensity;

    }

    public static void makeUniformDistribution(double[] distribution, int minValue, int maxValue) {
        
        double uniformProbability = 1.0 / (maxValue - minValue);
        Arrays.fill(distribution, 0, minValue, 0.0);
        Arrays.fill(distribution, minValue, maxValue, uniformProbability);
        Arrays.fill(distribution, maxValue, distribution.length, 0.0);

    }

    public static double[] getCumulativeDistribution(double[] distribution, boolean reverse) {

        double[] result = new double[distribution.length];

        double cumulativeProb = 0.0;
        if (reverse) {
            for(int i = distribution.length - 1; i >= 0; i--) {
                cumulativeProb += distribution[i];
                result[i] = cumulativeProb;
            }  
        } else {
            for(int i = 0; i < distribution.length; i++) {
                cumulativeProb += distribution[i];
                result[i] = cumulativeProb;
            } 
        }
        
        return result;
    }

    private static void predictWellBeingAndGrowthDistributions(
        CropsState initialState,
        List<double[]> wellBeingDistributions,
        List<double[]> growthDistributions
    ) {

        double pUnwell = (initialState == CropsState.HEALTHY) ? 0.0 : 1.0;
    
        int distrSize = agentsSettings.statesBinsAmount();
        double pHealing = 1.0 / agentsSettings.measuredAverageTaskCompletionTime();
        double pNeedingCare = 1.0 - (1.0 - cropsSettings.weedRemovalNeed()) * (1.0 - cropsSettings.wateringNeed());

        double[] growthDistribution = new double[distrSize];
        double[] wellBeingDistribution = new double[distrSize];
        double[] healthyGrowthIncreaseDistribution = new double[distrSize];
        double[] unwellGrowthIncreaseDistribution = new double[distrSize];
        double[] wellBeingDecreaseDistribution = new double[distrSize];


        initDistributions(
            distrSize,
            growthDistribution, 
            wellBeingDistribution, 
            healthyGrowthIncreaseDistribution, 
            unwellGrowthIncreaseDistribution, 
            wellBeingDecreaseDistribution
        );   


        for(int t = 0; t < agentsSettings.rewardPredictionSize(); t++) {

            // Update well being distribution
            wellBeingDistributions.add(getCumulativeDistribution(wellBeingDecreaseDistribution, false));
            
            double[] unwellDecrease = VectorUtils.multiply(pUnwell, wellBeingDecreaseDistribution);
            double[] noDecrease = new double[distrSize];
            makeImpulse(noDecrease, distrSize - 1, 1.0 - pUnwell);

            double[] decreaseRate = VectorUtils.sum(unwellDecrease, noDecrease); // this is slightly inefficient but I'm lazy

            wellBeingDistribution = VectorUtils.convolve(wellBeingDistribution, decreaseRate, -distrSize);

            // Update growth distribution
            growthDistributions.add(getCumulativeDistribution(growthDistribution, true));

            double[] healthyIncrease = VectorUtils.multiply(1.0 - pUnwell, healthyGrowthIncreaseDistribution);
            double[] unwellIncrease = VectorUtils.multiply(pUnwell, unwellGrowthIncreaseDistribution);

            double[] increaseRate = VectorUtils.sum(healthyIncrease, unwellIncrease);

            growthDistribution = VectorUtils.convolve(growthDistribution, increaseRate, 0);

            // Update State Probability: we are ignoring the Death State as we're going to use the cumulative probs to estimate
            // the probability of falling into the Death State, given the condition that we're currently not into it already
            pUnwell = pUnwell * (1 - pHealing) + (1.0 - pUnwell) * pNeedingCare;

        }

    }

    static {

        predictWellBeingAndGrowthDistributions(
            CropsState.HEALTHY,
            healthyWellBeingDistributions,
            healthyGrowthDistributions
        );

        predictWellBeingAndGrowthDistributions(
            CropsState.UNWELL,
            unwellWellBeingDistributions,
            unwellGrowthDistributions
        );
        
    }
    
}
