package com.sdai.smartfarm.environment.crops;

import java.io.Serializable;

public class CropsNeeds implements Serializable{
    
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

    @Override
    public boolean equals(Object o) {
        return (o instanceof CropsNeeds needs && (watering == needs.getWatering()) && (weedRemoval == needs.getWeedRemoval()));
    }

    @Override
    public int hashCode() {
        return ((watering) ? 1 << 1 : 0) + ((weedRemoval) ? 1 : 0);
    }

}
