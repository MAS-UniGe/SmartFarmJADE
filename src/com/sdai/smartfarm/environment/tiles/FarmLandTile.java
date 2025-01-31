package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;
import java.util.Random;

import com.sdai.smartfarm.environment.crops.Crops;
import com.sdai.smartfarm.environment.crops.CropsState;

public class FarmLandTile implements Tile {

    private final Crops crops;

    public FarmLandTile(final Random rng) {
        crops = new Crops(rng);
        crops.seed();
    }

    public Crops getCrops() {
        return crops;
    }

    @Override
    public TileType getType() {
        return TileType.FARMLAND;
    }

    @Override
    public Color getColor() {
        return new Color(
            Math.min((int)(crops.checkGrowth() * 125), 255), 
            Math.min((int)(crops.checkGrowth() * 125), 255),
            (crops.checkState() == CropsState.UNWELL) ? 100 : 30
        );
    }

    @Override
    public void update() {
        crops.update();
    }
}
