package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;
import java.util.Random;

import com.sdai.smartfarm.environment.crops.Crops;
import com.sdai.smartfarm.environment.crops.CropsState;

public class FarmLandTile implements Tile {

    private final Crops crops;

    public FarmLandTile(final Random rng) {
        crops = new Crops(rng);
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

        if(crops.checkState() == CropsState.MISSING) return new Color(80, 40, 0);

        if(crops.checkState() == CropsState.DEAD) return new Color(200, 175, 130);

        int red = 80 + (int)(160 * Math.min(crops.checkGrowth(), 1.0));
        int green = 40 + (int)(200 * Math.min(crops.checkGrowth(), 1.0));
        int blue = 0;

        if (crops.checkState() == CropsState.UNWELL) {
            red *= 0.65;
            green *= 0.85;
            blue = (int)(red * 0.2);
        }

        return new Color(red, green, blue);
    }

    public void seed(double cheat) {
        crops.seed(cheat);
    }

    public void seed() {
        crops.seed();
    }

    public double harvest() {
        return crops.harvest();
    }

    @Override
    public void update() {
        crops.update();
    }
}
