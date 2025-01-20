package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.Tile;

public class Environment {

    protected final int width;
    protected final int height;
    
    protected final Tile[] map;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;

        this.map = new Tile[width * height];

        initMap();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void initMap() {

        for(int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                map[row * width + col] = new FarmLandTile();
            }
        }
    }
    
}
