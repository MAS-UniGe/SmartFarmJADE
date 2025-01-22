package com.sdai.smartfarm.agents;

import com.sdai.smartfarm.environment.Environment;

import jade.core.Agent;

public abstract class FarmingAgentAbstract extends Agent implements FarmingAgent {
    
    protected int x;
    protected int y;
    protected transient Environment environment;

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void situate(Environment environment, Integer x, Integer y) {
        if(environment == null) 
            throw new IllegalArgumentException("situate: environment must be instantiated");

        this.environment = environment;

        boolean spawned = false;
        while(!spawned) {
            
            spawned = environment.trySpawn(getType(), x, y);
            if (!spawned) {
                x = environment.getRNG().nextInt(environment.getWidth());
                y = environment.getRNG().nextInt(environment.getHeight());
            }
            
        }

        this.x = x.intValue(); // cannot be null
        this.y = y.intValue();     

    }

}
