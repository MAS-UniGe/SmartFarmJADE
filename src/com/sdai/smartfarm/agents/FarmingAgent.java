package com.sdai.smartfarm.agents;

import com.sdai.smartfarm.environment.Environment;

public interface FarmingAgent {
    
    AgentType getType();

    int getX();

    int getY();

    void situate(Environment environment, Integer x, Integer y);

}
