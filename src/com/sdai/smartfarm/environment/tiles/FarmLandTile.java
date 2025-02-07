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
        return (crops.checkState() == CropsState.DEAD || crops.checkState() == CropsState.DECAYING) ? new Color(100, 50, 20)
        : (crops.checkState() == CropsState.UNWELL) ? new Color(170, 170, 60)
        : new Color(200, 200, 30);
    }

    /*public void setColor(Color color) {
        this.color = color;
    }*/

    @Override
    public void update() {
        crops.update();
    }
}
