package com.sdai.smartfarm.environment;

import java.awt.Color;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.PathTile;
import com.sdai.smartfarm.environment.tiles.TallObstacleTile;
import com.sdai.smartfarm.environment.tiles.Tile;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.models.Position;


public class Environment implements ObservableEnvironment {

    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());

    protected final Random rng = new Random();

    protected final int width;
    protected final int height;
    
    protected final Tile[] map;

    // I'm using a 2d array and not an hash table: heavier on the mmeory
    // but faster check on the surroundings
    protected final BaseFarmingAgent[] agentsMap;

    public Environment(int width, int height) {
        this.width = width;
        this.height = height;

        this.map = new Tile[width * height];

        this.agentsMap = new BaseFarmingAgent[width * height];

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

    public BaseFarmingAgent getAgentAt(int x, int y) {
        if (y < 0 || x < 0 || y >= height || x >= width) return null;
        return agentsMap[y * width + x];
    }

    public TileType[] getMap() {

        return Arrays.asList(map).stream().map(Tile::getType).toArray(TileType[]::new);

    }

    public boolean trySpawn(BaseFarmingAgent agent, Integer x, Integer y) {
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
                if(rng.nextFloat() < 0.003) {
                    map[y * width + x] = new TallObstacleTile();
                    continue;
                }

                if (y % 20 < 2 || y >= height - 2 || x % 40 < 2 || x >= width - 2)
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

    // needs to be synchronized to avoid race conditions between agents
    @Override
    public synchronized boolean moveAgent(BaseFarmingAgent agent, Position newPosition) {
        if(newPosition.x() < 0 || newPosition.x() >= width || newPosition.y() < 0 || newPosition.y() >= height) {
            LOGGER.severe("Agent " + agent.getLocalName() + " is trying to move out of bounds!");
            return false;
        }
        if(! newPosition.isAdjacent(agent.getPosition())) {
            LOGGER.severe("Agent " + agent.getLocalName() + " is trying to move too far away!");
            return false;
        }

        BaseFarmingAgent agentAtNewPosition = getAgentAt(newPosition.x(), newPosition.y());
        if (agentAtNewPosition != null && agentAtNewPosition != agent) {
            LOGGER.severe("Agent " + agent.getLocalName() + " is being blocked by " + agentAtNewPosition.getLocalName() + "!");
            return false;
        }

        agentsMap[agent.getPosition().y() * width + agent.getPosition().x()] = null;
        agentsMap[newPosition.y() * width + newPosition.x()] = agent;
        return true;
    }

    @Override
    public Observation observe(int xCenter, int yCenter, int radius) {
        int viewSize = 2 * radius + 1;

        TileType[] observedTiles = new TileType[viewSize * viewSize];
        AgentType[] observedAgents = new AgentType[viewSize * viewSize];

        for(int y = 0; y < viewSize; y++) {
            for(int x = 0; x < viewSize; x++) {

                Tile tile = getTile(xCenter + x - radius, yCenter + y - radius);
                if(tile != null) observedTiles[y * viewSize + x] = tile.getType();
    
                BaseFarmingAgent agent = getAgentAt(xCenter + x - radius, yCenter + y - radius);
                if(agent != null) observedAgents[y * viewSize + x] = agent.getType();

            }
        }

        CropsState cropsState = null;
        CropsNeeds cropsNeeds = null;
        double growth = 0.0;
        double wellBeing = 0.0;

        Tile tile = getTile(xCenter, yCenter);
        if(tile instanceof FarmLandTile farmLandTile) {
            cropsState = farmLandTile.getCrops().checkState();
            cropsNeeds = farmLandTile.getCrops().getNeeds();
            growth = farmLandTile.getCrops().checkGrowth();
            wellBeing = farmLandTile.getCrops().estimateWellBeing();
        }

        return new Observation(observedTiles, observedAgents, cropsState, cropsNeeds, growth, wellBeing);
    }

    @Override
    public void removeWeeds(int x, int y) {
        Tile tile = getTile(x, y);

        if (tile instanceof FarmLandTile farmlandTile && farmlandTile.getCrops().getNeeds().getWeedRemoval()) {
            farmlandTile.getCrops().removeWeeds();
        }
        
    }

    @Override
    public void water(int x, int y) {
        Tile tile = getTile(x, y);

        if (tile instanceof FarmLandTile farmlandTile && farmlandTile.getCrops().getNeeds().getWatering()) {
            farmlandTile.getCrops().water();
        }
        
    }

    @Override
    public void seed(int x, int y, double cheat) {
        Tile tile = getTile(x, y);

        if (tile instanceof FarmLandTile farmlandTile) {
            farmlandTile.seed(cheat);
        }
    }

    @Override
    public double harvest(int x, int y) {
        Tile tile = getTile(x, y);

        if (tile instanceof FarmLandTile farmlandTile) {
            return farmlandTile.harvest();
        }

        return 0.0;
    }
    
}
