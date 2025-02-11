package com.sdai.smartfarm.agents.drone;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.behaviours.DroneInitBehaviour;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.models.Position;

public class DroneAgent extends BaseFarmingAgent {

    /* STATIC STUFF */

    private static final Logger LOGGER = Logger.getLogger(DroneAgent.class.getName());

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return DroneAgent.instanceCounter++;
    }

    ///////////////////
    
    

    protected transient List<Position> assignedTiles;

    protected transient List<Position> pathPlan;

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

        registerToYellowPages();

        addBehaviour(new DroneInitBehaviour());
        
    }


    public List<Position> getAssignedTiles() {
        return assignedTiles;
    }
    public void setAssignedTiles(List<Position> assignedTiles) {
        this.assignedTiles = assignedTiles;

        if(LOGGER.isLoggable(Level.FINE)) {
            String message = String.format("%s - Assigned Tiles: %s", getName(), assignedTiles);
            LOGGER.fine(message);
        }
        
    } 

    public List<Position> getPathPlan() {
        return pathPlan;
    }

    public void setPathPlan(List<Position> pathPlan) {
        this.pathPlan = pathPlan;
    }
    
}
