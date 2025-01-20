package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;

public class PathTile implements Tile {
    
    @Override
    public TileType getType() {
        return TileType.PATH;
    }

    @Override
    public Color getColor() {
        return Color.GREEN;
    }
}
