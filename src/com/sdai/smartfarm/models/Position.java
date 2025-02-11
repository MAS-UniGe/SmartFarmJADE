package com.sdai.smartfarm.models;

import java.io.Serializable;

import com.sdai.smartfarm.settings.SimulationSettings;

public record Position(
    int x,
    int y
) implements Serializable {

    private static final SimulationSettings settings = SimulationSettings.defaultSimulationSettings();
    
    public boolean isAdjacent(Position otherPos) {
        return distance(otherPos) <= 1;
    }

    public int distance(Position otherPos) {
        return Math.abs(x - otherPos.x()) + Math.abs(y - otherPos.y());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Position pos && x == pos.x() && y == pos.y());
    }

    @Override
    public int hashCode() {
        return y * settings.mapWidth() + x;
    }

}
