package com.sdai.smartfarm.environment.crops;

public class CropsNeeds {
    
    private boolean watering = false;
    private boolean weedRemoval = false;

    public boolean getWatering() {
        return watering;
    }
    public void setWatering(boolean watering) {
        this.watering = watering;
    }

    public boolean getWeedRemoval() {
        return weedRemoval;
    }
    public void setWeedRemoval(boolean weedRemoval) {
        this.weedRemoval = weedRemoval;
    }

    public boolean isThereAny() {
        return watering || weedRemoval;
    }

}
