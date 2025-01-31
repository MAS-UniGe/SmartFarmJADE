package com.sdai.smartfarm.behaviours;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.settings.SimulationSettings;

import jade.core.behaviours.OneShotBehaviour;

public class InitBehaviour extends OneShotBehaviour {
    
    protected int width;
    protected int height;

    protected int[] fieldsMap;
    protected transient List<List<int[]>> fields;

    protected static SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();
    protected static AgentsSettings agentsSettings =  AgentsSettings.defaultAgentsSettings();

    private static final Logger logger = Logger.getLogger(InitBehaviour.class.getName());

    @Override
    public void action() {

        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        // We assume there has been some exploration beforehand and we completed a tile map of the static portion of the environment
        // Possible extension: Implement the initial exploration to build the map without "cheating"
        ObservableEnvironment environment = agent.getEnvironment();
        
        width = environment.getWidth();
        height = environment.getHeight();

        TileType[] observedMap = environment.getMap();

        splitIntoFields(observedMap);

        // Save initialization result
        agent.setFields(fields);
        agent.setFieldsMap(fieldsMap);

        logger.info(agent.getAID().getLocalName() + ": Finished Agent Initialization");

        agent.addBehaviour(new AgentDiscoveryBehaviour(agent, agentsSettings.agentDiscoveryInterval()));
        if (agent.getType() == AgentType.DRONE)
            agent.addBehaviour(new DroneConsumerBehaviour());

    }

    protected void splitIntoFields(TileType[] map) {

        int idealFieldSize = agentsSettings.idealFieldSize();

        fieldsMap = new int[width * height];
        Arrays.fill(fieldsMap, -1);

        fields = new ArrayList<>();

        fields.add(new ArrayList<>());
        
        int curFieldId = 0;

        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {

                int index = y * width + x;

                if(map[index] == TileType.FARMLAND && fieldsMap[index] == -1) {

                    fields.add(new ArrayList<>());

                    visit(map, x, y, curFieldId);

                    fields.get(curFieldId).sort(
                        Comparator.comparingInt((int[] v) -> v[1])
                            .thenComparingInt(v -> v[0])
                    );

              
                    int fieldSize = fields.get(curFieldId).size();

                    if (fieldSize > idealFieldSize) {
                        // then we gotta split the field
                        int numSplits = (fieldSize / idealFieldSize);
                        int splitSize = fieldSize / (numSplits + 1);

                        curFieldId = splitFieldIntoSubFields(curFieldId, numSplits, splitSize);
                        
                    }

                    curFieldId++;
                }

            }
            
        }
    }

    protected void visit(TileType[] map, int x, int y, int curFieldId) {

        if (x < 0 || x >= width || y < 0 || y >= height)
            return;

        int index = y * width + x;
        if (map[index] != TileType.FARMLAND || fieldsMap[index] != -1)
            return;
        
        fieldsMap[index] = curFieldId;
        fields.get(curFieldId).add(new int[] {x, y});

        visit(map, x-1, y, curFieldId);
        visit(map, x+1, y, curFieldId);
        visit(map, x, y-1, curFieldId);
        visit(map, x, y+1, curFieldId);
        
    }

    protected int splitFieldIntoSubFields(int curFieldId, int numSplits, int splitSize) {

        List<int[]> buffer = fields.remove(curFieldId);

        int start = 0;

        for (int i = 0; i < numSplits; i++) {
            // we split horizontally: it will never be a perfect split but this is better for the tractors
            int limitY = buffer.get(splitSize * (i + 1))[1];

            int splitIndex = start;

            // could be done more efficiently with alternated pop_first/push_back but I really do not trust Java's Arraylist implementation to be efficient
            for(int[] v : buffer.subList(start, buffer.size())) {

                fieldsMap[v[1] * width + v[0]] = curFieldId;

                if (v[1] == limitY)
                    break;

                splitIndex++;
            }

            List<int[]> splitField = new ArrayList<>(
                buffer.subList(start, splitIndex)
            );

            start = splitIndex;

            fields.add(splitField);
            curFieldId++;
        }

        for(int[] v : buffer.subList(start, buffer.size())) {

            fieldsMap[v[1] * width + v[0]] = curFieldId;
        }

        fields.add(new ArrayList<>(
            buffer.subList(start, buffer.size())
        ));
        
        return curFieldId;

    }

}
