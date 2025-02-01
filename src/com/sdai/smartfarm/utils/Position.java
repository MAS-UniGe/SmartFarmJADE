package com.sdai.smartfarm.utils;

import java.io.Serializable;

import com.sdai.smartfarm.settings.SimulationSettings;

public record Position(
    int x,
    int y
) implements Serializable {

    private static final SimulationSettings settings = SimulationSettings.defaultSimulationSettings();
    
    public boolean isAdjacent(Position otherPos) {
        return (x == otherPos.x && (y <= otherPos.y + 1 && y >= otherPos.y - 1)) 
            || (y == otherPos.y && (x <= otherPos.x + 1 && x >= otherPos.x - 1));
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
