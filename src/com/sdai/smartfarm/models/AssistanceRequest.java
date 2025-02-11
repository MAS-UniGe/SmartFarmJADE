package com.sdai.smartfarm.models;

import java.io.Serializable;

import com.sdai.smartfarm.environment.crops.CropsNeeds;

public record AssistanceRequest(
    Position position,
    CropsNeeds cropsNeeds
) implements Serializable {

    @Override
    public boolean equals(Object o) {
        return (o instanceof AssistanceRequest req && position.equals(req.position()) && cropsNeeds.equals(req.cropsNeeds()));
    }

    @Override
    public int hashCode() {
        return (position.hashCode() << 2) + cropsNeeds.hashCode();
    }
}
