package com.sdai.smartfarm.environment;

import java.util.Random;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.PathTile;
import com.sdai.smartfarm.environment.tiles.Tile;
import com.sdai.smartfarm.environment.tiles.TileType;

public class Environment {

    protected final Random rng = new Random();

    protected final int width;
    protected final int height;
    
    protected final Tile[] map;

    // I'm using a 2d array and not an hash table: heavier on the mmeory
    // but faster check on the surroundings
    protected final AgentType[] agentsMap;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;

        this.map = new Tile[width * height];

        this.agentsMap = new AgentType[width * height];

        initMap();
    }

    public Random getRNG() {
        return rng;
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

    public AgentType getAgentAt(int x, int y) {
        if (y < 0 || x < 0 || y >= height || x >= width) return null;
        return agentsMap[y * width + x];
    }

    public boolean trySpawn(AgentType agent, Integer x, Integer y) {
        if (x == null || y == null || y < 0 || x < 0 || y >= height || x >= width) 
            return false;

        Tile tile = getTile(x, y);  // cannot be null
            
        if (
            !tile.getType().equals(TileType.OBSTACLE) &&
            !tile.getType().equals(TileType.TALL_OBSTACLE) &&
            getAgentAt(x, y) == null
        ) {
            agentsMap[y * width + x] = agent;
            return true;
        }

        return false;
    }

    protected void initMap() {

        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y % 20 == 0 || y == height - 1 || x % 40 == 0 || x == width - 1)
                    map[y * width + x] = new PathTile();
                else 
                    map[y * width + x] = new FarmLandTile(rng);

            }
        }
    }

    public void update() {
        for(int i = 0; i < width * height; i++) {
            map[i].update();
        }
    }
    
}
