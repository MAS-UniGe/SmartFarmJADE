package com.sdai.smartfarm.models;

import java.io.Serializable;
import java.util.Deque;
import java.util.List;

public record FarmField(
    List<Position> positions,
    Deque<Double> totalRewardPrediction // Only tractors care about these, but I placed them here so I get them divided by field for free
) implements Serializable {

    public int size() {
        return positions.size();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof FarmField field && ((positions == null && field.positions == null) || (positions != null && positions.equals(field.positions))));
    }

    @Override
    public int hashCode() {
        return positions.hashCode();
    }
}

