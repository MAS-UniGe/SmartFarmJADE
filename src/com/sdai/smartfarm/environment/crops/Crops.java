package com.sdai.smartfarm.environment.crops;

import java.util.Random;

import com.sdai.smartfarm.settings.CropsSettings;

public class Crops {

    private static CropsSettings settings = CropsSettings.defaultCropsSettings();

    public static void setCropsSettings(CropsSettings settings) {
        Crops.settings = settings;
    }

    private final Random rng;

    private CropsState state = CropsState.MISSING;

    private CropsNeeds needs = new CropsNeeds();

    private double growth = 0.0;

    private double wellBeing = 1.0;

    public Crops(final Random rng) {
        this.rng = rng;
    }

    public CropsState checkState() {
        return state;
    }

    public double checkGrowth() {
        return growth;
    }

    public CropsNeeds getNeeds() {
        return needs;
    }

    public void seed() {
        if (state == CropsState.MISSING) {
            state = CropsState.GROWING;
            growth = 0.0;
        }
    }

    public double harvest() {
        double cropYield = 0.0;

        if (state != CropsState.DEAD && growth >= 1.0) 
            cropYield = rng.nextDouble(0.8, 1.2) * wellBeing;

        state = CropsState.MISSING;
        growth = 0.0;

        return cropYield;
    }

    private void createNeeds() {
        if (rng.nextDouble() < settings.wateringNeed()) {
            needs.setWatering(true);
            state = CropsState.UNWELL;
        }
        if (rng.nextDouble() < settings.weedRemovalNeed()) {
            needs.setWeedRemoval(true);
            state = CropsState.UNWELL;
        }
    }

    public void update() {

        switch (state) {
            case GROWING:
                growth += rng.nextDouble(0.5, 1.0) * settings.saneGrowthRate();
                if (growth > settings.growthLimit() && rng.nextDouble() < settings.decayChance())
                    state = CropsState.DECAYING;
                else 
                    createNeeds();
                break;

            case UNWELL:
                growth += rng.nextDouble(0.5, 1.0) * settings.unwellGrowthRate();
                wellBeing -= settings.wellBeingDecay();
                if (wellBeing < settings.wellBeingThreshold() || (growth > settings.growthLimit() && rng.nextDouble() < settings.decayChance()))
                    state = CropsState.DECAYING;
                else 
                    createNeeds();
                break;

            case DECAYING:
                if(rng.nextDouble() < settings.dyingChance()) state = CropsState.DEAD;
                break;
            default:
                break;
        }

    }
    
}
