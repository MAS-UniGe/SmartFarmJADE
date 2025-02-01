package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.environment.tiles.TileType;

public record ObservedEnvironment(
    TileType[] map,
    int width,
    int height
) {
    
}
