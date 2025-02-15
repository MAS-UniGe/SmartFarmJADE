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
            Double.valueOf(System.getenv().getOrDefault("CROPS_DYING_CHANCE", "3e-6")), 
            Double.valueOf(System.getenv().getOrDefault("CROPS_DECAY_CHANCE", "5e-6")), 
            Double.valueOf(System.getenv().getOrDefault("CROPS_WELL_BEING_THRESHOLD", "0.4")), 
            Double.valueOf(System.getenv().getOrDefault("CROPS_WELL_BEING_DECREASE", "2.0e-5")), 
            Double.valueOf(System.getenv().getOrDefault("CROPS_HEALTHY_GROWTH_RATE", "1.9e-5")),
            Double.valueOf(System.getenv().getOrDefault("CROPS_UNWELL_GROWTH_RATE", "1.4e-5")),
            Double.valueOf(System.getenv().getOrDefault("CROPS_WATERING_NEED", "1.6e-4")),
            Double.valueOf(System.getenv().getOrDefault("CROPS_WEED_REMOVAL_NEED", "1.6e-4"))
        );
    }
}