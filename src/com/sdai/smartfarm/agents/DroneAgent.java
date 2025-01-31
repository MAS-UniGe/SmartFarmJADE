package com.sdai.smartfarm.agents;

import java.util.List;

import com.sdai.smartfarm.behaviors.ExploringBehaviour;
import com.sdai.smartfarm.behaviors.InitBehaviour;
import com.sdai.smartfarm.behaviors.MasterDroneInitBehavior;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.settings.AgentsSettings;

import elki.data.IntegerVector;

public class DroneAgent extends BaseFarmingAgent {

    /* STATIC STUFF */

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return DroneAgent.instanceCounter++;
    }

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    public static void setCropsSettings(AgentsSettings settings) {
        DroneAgent.settings = settings;
    }

    ///////////////////
    
    // TODO: add clusterization

    @Override
    public AgentType getType() {
        return AgentType.DRONE;
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null || args.length < 2) 
            throw new IllegalArgumentException("setup: not enough arguments");
        
        Environment environment = (Environment) args[0];
        int id = (int) args[1];

        Integer x = null;
        Integer y = null;

        if (args.length >= 4) {
            x = (int) args[2];
            y = (int) args[3];
        }

        situate(environment, x, y);

        this.environment = environment;

        if(id == 0) {
            addBehaviour(new MasterDroneInitBehavior());
        } else {
            //addBehaviour(new InitBehaviour());
        }
        //addBehaviour(new ExploringBehaviour(this, (long) (1000 / settings.droneSpeed())));
    }

    
    
}
