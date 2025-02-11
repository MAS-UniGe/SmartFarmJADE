package com.sdai.smartfarm.settings;

public record CropsSettings(
    double dyingChance,
    double decayChance,
    double wellBeingThreshold,
    double wellBeingDecrease,
    double healthyGrowthRate,
    double unwellGrowthRate,
    double wateringNeed,
    double weedRemovalNeed
) {

    public static CropsSettings defaultCropsSettings() {
        return new CropsSettings(
            1e-5, 
            1.2e-5, 
            0.4, 
            2.2e-5, 
            1.9e-5, 
            1.4e-5,
            1.6e-4, 
            1.6e-4
        );
    }
}
