package com.sdai.smartfarm.agents;

import com.sdai.smartfarm.environment.Environment;

public class DroneAgent extends FarmingAgentAbstract {

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return DroneAgent.instanceCounter++;
    }

    @Override
    public AgentType getType() {
        return AgentType.DRONE;
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null || args.length < 1) 
            throw new IllegalArgumentException("setup: not enough arguments");
        
        this.environment = (Environment) args[0];
    
        Integer x = null;
        Integer y = null;

        if (args.length >= 3) {
            x = (int) args[1];
            y = (int) args[2];
        }

        situate(environment, x, y);

        ///addBehaviour(null);
    }
    
}
