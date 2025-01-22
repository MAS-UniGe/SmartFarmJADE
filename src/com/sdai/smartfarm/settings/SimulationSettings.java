package com.sdai.smartfarm.settings;

public record SimulationSettings(
    int mapWidth,
    int mapHeight,
    int dronesNumber
) {

    public static SimulationSettings defaultSimulationSettings() {
        return new SimulationSettings(
            100, 
            100,
            10
        );
    }
    
}
