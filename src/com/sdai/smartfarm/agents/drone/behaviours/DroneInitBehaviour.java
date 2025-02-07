package com.sdai.smartfarm.agents.drone.behaviours;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.common_behaviours.InitBehaviour;
import com.sdai.smartfarm.settings.AgentsSettings;

public class DroneInitBehaviour extends InitBehaviour {
    
    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    @Override
    protected void addBehaviours(BaseFarmingAgent agent) {

        super.addBehaviours(agent);
        agent.addBehaviour(new PathPlanningBehaviour());
        agent.addBehaviour(new CheckOnCropsBehaviour((DroneAgent) agent, (long) (1000.0 / settings.droneSpeed())));

    } 
}
