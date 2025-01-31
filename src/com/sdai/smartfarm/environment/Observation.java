package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.environment.tiles.TileType;

public record Observation(
    TileType[] tiles,
    AgentType[] agents,
    CropsState cropsState,
    CropsNeeds needs
) {
    
}
