package com.sdai.smartfarm.settings;


public record SimulationSettings(
    int mapWidth,
    int mapHeight,
    int targetFPS,
    double targetUPS, // 1 update = 100 real life seconds
    float mouseSensitivity,
    int dronesNumber,
    int robotsNumber,
    int tractorsNumber,
    boolean growthCheat
) {

    public static SimulationSettings defaultSimulationSettings() {
        return new SimulationSettings(
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_MAP_WIDTH", "60")), 
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_MAP_HEIGHT", "60")),
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_TARGET_FPS", "80")),
            Double.valueOf(System.getenv().getOrDefault("SIMULATION_TARGET_UPS", "10")),
            Float.valueOf(System.getenv().getOrDefault("SIMULATION_MOUSE_SENSITIVITY", "1.0")),
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_DRONES_NUMBER", "4")),
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_ROBOTS_NUMBER", "6")),
            Integer.valueOf(System.getenv().getOrDefault("SIMULATION_TRACTORS_NUMBER", "1")), 
            Boolean.parseBoolean(System.getenv().getOrDefault("SIMULATION_GROWTH_CHEAT", "false"))
        );
    }
    
}
