package com.sdai.smartfarm.behaviors;

import java.util.Random;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.FarmingAgent;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.Observation;
import com.sdai.smartfarm.environment.crops.Crops;
import com.sdai.smartfarm.environment.crops.CropsNeeds;
import com.sdai.smartfarm.environment.crops.CropsState;
import com.sdai.smartfarm.environment.tiles.FarmLandTile;
import com.sdai.smartfarm.environment.tiles.Tile;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.behaviours.TickerBehaviour;

public class ExploringBehaviour extends TickerBehaviour {

    private static AgentsSettings agentSettings = AgentsSettings.defaultAgentsSettings();

    private Random rng = new Random();

    private transient ObservableEnvironment environment;

    public ExploringBehaviour(BaseFarmingAgent agent, long period) {
        super(agent, period);

        environment = agent.getEnvironment();
    }

    @Override
    protected void onTick() {
        
        FarmingAgent agent = (FarmingAgent) myAgent;

        int x = agent.getX();
        int y = agent.getY();

        Observation observation = environment.observe(x, y, agentSettings.viewRange());

        int xDir = rng.nextInt(-1, 2);
        int yDir = rng.nextInt(-1, 2);
        
        int xTo = x + xDir;
        int yTo = y + yDir;

        boolean success = environment.moveAgent(x, y, xTo, yTo);

        if (success) {
            agent.setX(xTo);
            agent.setY(yTo);
        }
        
    }
    
}
