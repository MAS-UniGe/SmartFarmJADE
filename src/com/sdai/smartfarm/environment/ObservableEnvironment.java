package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.utils.Position;

public interface ObservableEnvironment {

    Observation observe(int xCenter, int yCenter, int radius);

    boolean moveAgent(BaseFarmingAgent agent, Position newPosition);

    void removeWeeds(int x, int y);

    void water(int x, int y);
    
}
