package com.sdai.smartfarm.settings;

public record AgentsSettings(
    int viewRange,
    double droneSpeed,
    double robotSpeed,
    double tractorSpeed,
    long robotWateringDelay,
    long robotWeedingDelay,
    long tractorClockDelay,
    long agentDiscoveryInterval,
    int rewardPredictionSize,
    int statesBinsAmount,
    int idealFieldSize,
    double minimumRewardPercentage,
    double measuredAverageTaskCompletionTime
) {

    public static AgentsSettings defaultAgentsSettings() {
        return new AgentsSettings(
            Integer.valueOf(System.getenv().getOrDefault("AGENTS_VIEW_RANGE", "3")), 
            Double.valueOf(System.getenv().getOrDefault("AGENTS_DRONE_SPEED", "500")), // 2km/h if tiles are 1mx1m
            Double.valueOf(System.getenv().getOrDefault("AGENTS_ROBOT_SPEED", "250")), 
            Double.valueOf(System.getenv().getOrDefault("AGENTS_TRACTOR_SPEED", "120")),
            Long.valueOf(System.getenv().getOrDefault("AGENTS_ROBOT_WATERING_DELAY", "8")), // realistic (?) times to complete the tasks
            Long.valueOf(System.getenv().getOrDefault("AGENTS_ROBOT_WEEDING_DELAY", "12")), //
            Long.valueOf(System.getenv().getOrDefault("AGENTS_TRACTOR_CLOCK_DELAY", "100")),
            Long.valueOf(System.getenv().getOrDefault("AGENTS_AGENT_DISCOVERY_INTERVAL", "4000")), 
            Integer.valueOf(System.getenv().getOrDefault("AGENTS_REWARD_PREDICTION_SIZE", "500")),
            Integer.valueOf(System.getenv().getOrDefault("AGENTS_STATES_BINS_AMOUNT", "1000")),
            Integer.valueOf(System.getenv().getOrDefault("AGENTS_IDEAL_FIELD_SIZE", "1000")),
            Double.valueOf(System.getenv().getOrDefault("AGENTS_MINIMUM_REWARD_PERCENTAGE", "0.1")),
            Double.valueOf(System.getenv().getOrDefault("AGENTS_MEASURED_AVERAGE_COMPLETION_TIME", "30.0"))
        );
    }
    
}
