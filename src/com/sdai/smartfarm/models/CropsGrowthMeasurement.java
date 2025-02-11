package com.sdai.smartfarm.models;

import java.io.Serializable;

import com.sdai.smartfarm.environment.crops.CropsState;

public record CropsGrowthMeasurement(
    Position position,
    double growth,
    double wellBeing,
    CropsState state
) implements Serializable {

}
