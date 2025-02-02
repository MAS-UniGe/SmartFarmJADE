package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;

public class TallObstacleTile implements Tile {
    
    @Override
    public TileType getType() {
        return TileType.TALL_OBSTACLE;
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    @Override
    public void update() {
        
    }

}
