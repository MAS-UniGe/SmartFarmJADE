package com.sdai.smartfarm.environment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.FarmingAgent;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.PathTile;
import com.sdai.smartfarm.environment.tiles.Tile;
import com.sdai.smartfarm.environment.tiles.TileType;

import elki.data.IntegerVector;
import tutorial.clustering.SameSizeKMeans;

public class Environment implements ObservableEnvironment {

    protected final Random rng = new Random();

    protected final int width;
    protected final int height;
    
    protected final Tile[] map;

    // I'm using a 2d array and not an hash table: heavier on the mmeory
    // but faster check on the surroundings
    protected final FarmingAgent[] agentsMap;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;

        this.map = new Tile[width * height];

        this.agentsMap = new FarmingAgent[width * height];

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

    public FarmingAgent getAgentAt(int x, int y) {
        if (y < 0 || x < 0 || y >= height || x >= width) return null;
        return agentsMap[y * width + x];
    }

    public boolean trySpawn(FarmingAgent agent, Integer x, Integer y) {
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
                else {
                    map[y * width + x] = new FarmLandTile(rng);

                }

            }
        }

    }

    
    public void update() {
        for(int i = 0; i < width * height; i++) {
            map[i].update();
        }
    }

    @Override
    public boolean moveAgent(int xFrom, int yFrom, int xTo, int yTo) {
        if (yFrom < 0 || yFrom >= height || yTo < 0 || yTo >= height) return false;
        if (xFrom < 0 || xFrom >= width  || xTo < 0 || xTo >= width ) return false;
        if (getAgentAt(xFrom, yFrom) == null || getAgentAt(xTo, yTo) != null) return false;

        agentsMap[yTo * width + xTo] = agentsMap[yFrom * width + xFrom];
        agentsMap[yFrom * width + xFrom] = null;
        return true;
    }

    @Override
    public TileType[] getMap() {

        return Arrays.asList(map).stream().map(Tile::getType).toArray(TileType[]::new);

    };

    @Override
    public Observation observe(int xCenter, int yCenter, int radius) {
        TileType[] observedTiles = new TileType[(radius + 1) * (radius + 1)];
        AgentType[] observedAgents = new AgentType[(radius + 1) * (radius + 1)];

        for(int y = yCenter - radius; y < yCenter + radius + 1; y++) {
            for(int x = xCenter - radius; x < xCenter + radius + 1; x++) {

                Tile tile = getTile(x, y);
                if(tile != null) observedTiles[y * width + x] = tile.getType();
    
                FarmingAgent agent = getAgentAt(x, y);
                if(agent != null) observedAgents[y * width + x] = agent.getType();

            }
        }

        CropsState cropsState = null;
        CropsNeeds cropsNeeds = null;

        Tile tile = getTile(xCenter, yCenter);
        if(tile instanceof FarmLandTile farmLandTile) {
            cropsState = farmLandTile.getCrops().checkState();
            cropsNeeds = farmLandTile.getCrops().getNeeds();
        }

        return new Observation(observedTiles, observedAgents, cropsState, cropsNeeds);
    }
    
}
