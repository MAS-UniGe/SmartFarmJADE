package com.sdai.smartfarm.agents;

import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.behaviours.InitBehaviour;
import com.sdai.smartfarm.environment.Environment;

public class DroneAgent extends BaseFarmingAgent {

    /* STATIC STUFF */

    private static final Logger logger = Logger.getLogger(DroneAgent.class.getName());

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return DroneAgent.instanceCounter++;
    }

    ///////////////////
    
    

    protected transient List<int[]> assignedTiles;

    @Override
    public AgentType getType() {
        return AgentType.DRONE;
    }

    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null || args.length < 1) 
            throw new IllegalArgumentException("setup: not enough arguments");
        
        Environment environment = (Environment) args[0];

        Integer x = null;
        Integer y = null;

        if (args.length >= 3) {
            x = (int) args[1];
            y = (int) args[2];
        }

        situate(environment, x, y);

        this.environment = environment; // From now on it's just an "ObservableEnvironment" -> a.k.a. we're almost done cheating

        registerToYellowPages();

        addBehaviour(new InitBehaviour());
        
    }


    public List<int[]> getAssignedTiles() {
        return assignedTiles;
    }
    public void setAssignedTiles(List<int[]> assignedTiles) {
        this.assignedTiles = assignedTiles;

        String message = String.format("%s - Assigned Tiles: %s", getName(), assignedTiles.toString());
        logger.info(message);
    } 
    
}
