package com.sdai.smartfarm.settings;

public record SimulationSettings(
    int mapWidth,
    int mapHeight,
    int targetFPS,
    int targetUPS,
    float mouseSensitivity,
    int dronesNumber
) {

    public static SimulationSettings defaultSimulationSettings() {
        return new SimulationSettings(
            100, 
            100,
            60,
            80,
            1.0f,
            8
        );
    }
    
}
