package com.sdai.smartfarm.agents.drone.behaviours;

import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.common_behaviours.FollowPathBehaviour;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.settings.AgentsSettings;

public class CheckOnCropsBehaviour extends FollowPathBehaviour {

    protected transient AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    public CheckOnCropsBehaviour(DroneAgent agent, long period) {
        
        super(agent, period, agent.getPathPlan());

    }

    @Override
    protected Observation observeAndUpdate() {

        Observation observation = super.observeAndUpdate();

        DroneAgent agent = (DroneAgent) getAgent();

        CropsState cropsState = observation.cropsState();
        CropsNeeds needs = observation.needs();

        if(cropsState == CropsState.UNWELL) {
            agent.addBehaviour(new RequestAssistanceBehaviour(agent.getPosition(), needs));
        }
        
        return observation;
    }
    
    @Override
    public void stop() {
        super.stop();

        DroneAgent agent = (DroneAgent) getAgent();

        // You may wonder, why readd a new behaviour when we could reset the iterator to 0?
        // The reason is that we want the pathPlan to be used instead of the just produced path:
        // since every FIXED obstacle is already accounted for in the pathPlan, everything else
        // may have moved out the way in the mean time. 
        agent.addBehaviour(new CheckOnCropsBehaviour(agent, this.getPeriod()));
    }

}
