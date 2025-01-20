package com.sdai.smartfarm.environment.tiles;

import java.awt.Color;

/**
 *  Generic Tile for the environment
 */
public interface Tile {
    
    TileType getType();

    Color getColor();
}
