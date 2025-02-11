package com.sdai.smartfarm.settings;

public record SimulationSettings(
    int mapWidth,
    int mapHeight,
    int targetFPS,
    double targetUPS, // 1 update = 100 real life seconds
    float mouseSensitivity,
    int dronesNumber,
    int robotsNumber,
    int tractorsNumber
) {

    public static SimulationSettings defaultSimulationSettings() {
        return new SimulationSettings(
            100, 
            100,
            80,
            10,
            1.0f,
            2,
            2,
            1 // Right now there can only be one tractor!
        );
    }
    
}
