package com.sdai.smartfarm.settings;

public record AgentsSettings(
    int viewRange,
    double droneSpeed,
    int agentDiscoveryInterval,
    int idealFieldSize
) {

    public static AgentsSettings defaultAgentsSettings() {
        return new AgentsSettings(3, 1.0, 15000, 50);
    }
    
}
