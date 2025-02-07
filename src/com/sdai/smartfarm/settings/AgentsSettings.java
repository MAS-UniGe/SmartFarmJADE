package com.sdai.smartfarm.settings;

public record AgentsSettings(
    int viewRange,
    double droneSpeed,
    double robotSpeed,
    int agentDiscoveryInterval,
    int idealFieldSize
) {

    public static AgentsSettings defaultAgentsSettings() {
        return new AgentsSettings(3, 80.0, 40.0, 5000, 1000);
    }
    
}
