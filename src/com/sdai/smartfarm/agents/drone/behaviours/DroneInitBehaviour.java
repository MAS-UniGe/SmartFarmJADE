package com.sdai.smartfarm.agents.drone.behaviours;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.common_behaviours.InitBehaviour;
import com.sdai.smartfarm.common_behaviours.ReceiveHarvestNotificationBehaviour;
import com.sdai.smartfarm.settings.AgentsSettings;

public class DroneInitBehaviour extends InitBehaviour {
    
    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    @Override
    protected void addBehaviours(BaseFarmingAgent agent) {

        // Yes, I want a ClassCastException here if you give something called "DroneInitBehaviour" to anything but a Drone
        if (!(agent instanceof DroneAgent droneAgent)) throw new ClassCastException("DroneInitBehaviour should be given only to a Drone!");

        super.addBehaviours(droneAgent);
        droneAgent.addBehaviour(new ReceiveHarvestNotificationBehaviour());
        droneAgent.addBehaviour(new PathPlanningBehaviour());
        droneAgent.addBehaviour(new CheckOnCropsBehaviour(droneAgent, (long) (1000.0 / settings.droneSpeed())));

    } 
}
