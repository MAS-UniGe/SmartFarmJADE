package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;
import java.util.Random;

import com.sdai.smartfarm.environment.crops.Crops;
import com.sdai.smartfarm.environment.crops.CropsState;

public class FarmLandTile implements Tile {

    private final Crops crops;

    public FarmLandTile(final Random rng) {
        crops = new Crops(rng);

        // I initialize everything as already seeded as I don't have enough time to implement this step as well
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

        if(crops.checkState() == CropsState.DEAD) return new Color(200, 185, 100);

        int red = 80 + (int)(160 * crops.checkGrowth());
        int green = 40 + (int)(200 * crops.checkGrowth());
        int blue = 0;

        if (crops.checkState() == CropsState.UNWELL) {
            red *= 0.65;
            green *= 0.85;
            blue = (int)(red * 0.2);
        }

        return new Color(red, green, blue);
    }

    public double harvest() {
        return crops.harvest();
    }

    @Override
    public void update() {
        crops.update();
    }
}
