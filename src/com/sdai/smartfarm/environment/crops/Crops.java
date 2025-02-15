package com.sdai.smartfarm.environment.crops;

import java.util.Random;

import com.sdai.smartfarm.settings.CropsSettings;

public class Crops {

    private static CropsSettings settings = CropsSettings.defaultCropsSettings();

    public static void setCropsSettings(CropsSettings settings) {
        Crops.settings = settings;
    }

    private final Random rng;

    private final double growthLimit = 1.0;

    private CropsState state = CropsState.MISSING;

    private CropsNeeds needs = new CropsNeeds();

    private double growth = 0.0;

    private double wellBeing = 1.0;

    private boolean decaying = false;

    public Crops(final Random rng) {
        this.rng = rng;
    }

    public CropsState checkState() {
        return state;
    }

    public void removeWeeds() {
        needs.setWeedRemoval(false);
        if(!needs.isThereAny() && state != CropsState.DEAD && state != CropsState.MISSING) {
            state = CropsState.HEALTHY;
        }
    }

    public void water() {
        needs.setWatering(false);
        if(!needs.isThereAny() && state != CropsState.DEAD && state != CropsState.MISSING) {
            state = CropsState.HEALTHY;
        }
    }

    // TODO: Maybe add measurement error?
    public double checkGrowth() {
        return growth;
    }

    // TODO: Maybe add measurement error?
    public double estimateWellBeing() {
        return wellBeing;
    }

    public CropsNeeds getNeeds() {
        return needs;
    }

    public void seed() {
        seed(0.0);
    }

    public void seed(double cheat) {
        if (state == CropsState.MISSING) {
            state = CropsState.HEALTHY;
            growth = cheat;
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

        if (state == CropsState.MISSING) return;

        if (decaying && rng.nextDouble() < settings.dyingChance()) 
            state = CropsState.DEAD;

        if (state == CropsState.UNWELL && !needs.isThereAny()) // then it has been healed/supported/watered/whatever
            state = CropsState.HEALTHY;

        switch (state) {
            case HEALTHY:
                if(!decaying)
                    growth += rng.nextDouble(0.5, 1.0) * settings.healthyGrowthRate();
                if (growth > growthLimit && rng.nextDouble() < settings.decayChance())
                    decaying = true;
                else 
                    createNeeds();
                break;

            case UNWELL:
                if(!decaying) {
                    growth += rng.nextDouble(0.5, 1.0) * settings.unwellGrowthRate();
                    wellBeing -= rng.nextDouble(0.5, 1.0) * settings.wellBeingDecrease();
                }
                if (wellBeing < settings.wellBeingThreshold() || (growth > growthLimit && rng.nextDouble() < settings.decayChance()))
                    decaying = true;
                else
                    createNeeds();
                break;

            default: // DEAD
                return;

        }

    }
    
}
