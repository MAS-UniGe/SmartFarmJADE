package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;

public class FarmLandTile implements Tile {
    
    @Override
    public TileType getType() {
        return TileType.FARMLAND;
    }

    @Override
    public Color getColor() {
        return new Color(170, 100, 30);
    }
}
