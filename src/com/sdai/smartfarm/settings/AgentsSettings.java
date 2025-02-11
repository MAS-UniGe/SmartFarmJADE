package com.sdai.smartfarm.settings;

public record AgentsSettings(
    int viewRange,
    double droneSpeed,
    double robotSpeed,
    double tractorSpeed,
    int agentDiscoveryInterval,
    int rewardPredictionSize,
    int statesBinsAmount,
    int idealFieldSize,
    double minimumRewardPercentage,
    double measuredAverageTaskCompletionTime
) {

    private static SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();

    public static AgentsSettings defaultAgentsSettings() {
        return new AgentsSettings(
            3, 
            100.0 * simulationSettings.targetUPS(), // 2km/h if tiles are 1mx1m
            54.0 * simulationSettings.targetUPS(), 
            27.0 * simulationSettings.targetUPS(),
            5000, 
            500,
            1000,
            1000,
            0.4,
            30.0
        );
    }
    
}
