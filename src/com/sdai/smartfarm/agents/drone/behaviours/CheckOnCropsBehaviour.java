package com.sdai.smartfarm.agents.drone.behaviours;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.common_behaviours.FollowPathBehaviour;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.utils.Position;

public class CheckOnCropsBehaviour extends FollowPathBehaviour {

    protected transient AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    public CheckOnCropsBehaviour(DroneAgent agent, long period) {
        
        super(agent, period, agent.getPathPlan());

    }

    protected void observeAndUpdate() {

        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        Position currentPosition = agent.getPosition();

        Observation observation = agent.getEnvironment().observe(currentPosition.x(), currentPosition.y(), settings.viewRange());

        ObservedEnvironment observedEnvironment = agent.getObservedEnvironment();

        int viewSize = 2 * settings.viewRange() + 1;

        for (int y = 0; y < viewSize; y++) {
            for (int x = 0; x < viewSize; x++) {

                TileType observedTile = observation.tiles()[y * viewSize + x];
                AgentType observedAgent = observation.agents()[y * viewSize + x];

                int mapY = currentPosition.y() + y - settings.viewRange();
                int mapX = currentPosition.x() + x - settings.viewRange();

                if (observedTile != null) {
                    observedEnvironment.map()[mapY * observedEnvironment.width() + mapX] = observedTile;
                }

                if (observedAgent != null && (mapX != currentPosition.x() || mapY != currentPosition.y())) {
                    // Yes, we consider other agents obstacles
                    observedEnvironment.map()[mapY * observedEnvironment.width() + mapX] = TileType.TALL_OBSTACLE;
                }
                
            }
        }
        
        
    }

    @Override
    protected void onTick() {

        observeAndUpdate();

        super.onTick();


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
