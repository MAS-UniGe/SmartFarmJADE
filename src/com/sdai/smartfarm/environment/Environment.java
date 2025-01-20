package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.PathTile;
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

    public Tile getTile(int x, int y) {
        if (y < 0 || x < 0 || y >= height || x >= width) return null;
        return map[y * width + x];
    }

    protected void initMap() {

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y % 20 == 0 || y == height - 1 || x % 40 == 0 || x == width - 1)
                    map[y * width + x] = new PathTile();
                else
                    map[y * width + x] = new FarmLandTile();
            }
        }
    }
    
}
