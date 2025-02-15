package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.models.Position;

public interface ObservableEnvironment {

    Observation observe(int xCenter, int yCenter, int radius);

    boolean moveAgent(BaseFarmingAgent agent, Position newPosition);

    void seed(int x, int y, double cheat); // the cheat is here to avoid running the simulation for 2 hours if we just need to test tractors

    void removeWeeds(int x, int y);

    void water(int x, int y);

    double harvest(int x, int y);
    
}
