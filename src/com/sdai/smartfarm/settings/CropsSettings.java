package com.sdai.smartfarm.settings;

public record CropsSettings(
    double growthLimit,
    double dyingChance,
    double decayChance,
    double wellBeingThreshold,
    double wellBeingDecay,
    double saneGrowthRate,
    double unwellGrowthRate,
    double wateringNeed,
    double weedRemovalNeed
) {

    public static CropsSettings defaultCropsSettings() {
        return new CropsSettings(
            1.2,
            0.0001, 
            0.0001, 
            0.4, 
            0.0003, 
            0.002, 
            0.001,
            0.001, 
            0.001
        );
    }
}
