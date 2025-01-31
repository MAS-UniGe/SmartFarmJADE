package com.sdai.smartfarm.behaviors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sdai.smartfarm.agents.FarmingAgent;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.settings.AgentsSettings;
import com.sdai.smartfarm.settings.SimulationSettings;

import elki.data.IntegerVector;
import jade.core.behaviours.OneShotBehaviour;

public class InitBehaviour extends OneShotBehaviour {
    
    protected int width;
    protected int height;

    protected int[] fieldsMap;
    protected transient List<List<IntegerVector>> fields;

    protected static SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();
    protected static AgentsSettings agentsSettings =  AgentsSettings.defaultAgentsSettings();

    @Override
    public void action() {

        FarmingAgent agent = (FarmingAgent) getAgent();

        // We assume there has been some exploration beforehand and we completed a tile map of the static portion of the environment
        // Possible extension: Implement the initial exploration to build the map without "cheating"
        ObservableEnvironment environment = agent.getEnvironment();
        
        width = simulationSettings.mapWidth();
        height = simulationSettings.mapHeight();

        TileType[] observedMap = environment.getMap();

        splitIntoFields(observedMap);

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
                        Comparator.comparingInt((IntegerVector v) -> v.intValue(1))
                            .thenComparingInt(v -> v.intValue(0))
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
        fields.get(curFieldId).add(new IntegerVector(new int[] {x, y}));

        visit(map, x-1, y, curFieldId);
        visit(map, x+1, y, curFieldId);
        visit(map, x, y-1, curFieldId);
        visit(map, x, y+1, curFieldId);
        
    }

    protected int splitFieldIntoSubFields(int curFieldId, int numSplits, int splitSize) {

        List<IntegerVector> buffer = fields.remove(curFieldId);

        int start = 0;

        for (int i = 0; i < numSplits; i++) {
            // we split horizontally: it will never be a perfect split but this is better for the tractors
            int limitY = buffer.get(splitSize * (i + 1)).intValue(1);

            int splitIndex = 0;

            // could be done more efficiently with alternated pop_first/push_back but I really do not trust Java's Arraylist implementation to be efficient
            for(IntegerVector v : buffer.subList(start, buffer.size())) {

                fieldsMap[v.intValue(1) * width + v.intValue(0)] = curFieldId;

                if (v.intValue(1) == limitY)
                    break;

                splitIndex++;
            }

            List<IntegerVector> splitField = new ArrayList<>(
                buffer.subList(start, splitIndex)
            );

            start = splitIndex;

            fields.add(splitField);
            curFieldId++;
        }

        for(IntegerVector v : buffer.subList(start, buffer.size())) {

            fieldsMap[v.intValue(1) * width + v.intValue(0)] = curFieldId;
        }

        fields.add(new ArrayList<>(
            buffer.subList(start, buffer.size())
        ));
        
        return curFieldId;

    }

}
