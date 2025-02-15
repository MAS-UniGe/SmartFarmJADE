package com.sdai.smartfarm.settings;

public record WindowSettings(
    int width,
    int height,
    int gridSize,
    int minTileSize,
    int maxTileSize
) {

    public static WindowSettings defaultWindowSettings() {
        return new WindowSettings(
            Integer.valueOf(System.getenv().getOrDefault("WINDOW_WIDTH", "500")),
            Integer.valueOf(System.getenv().getOrDefault("WINDOW_HEIGHT", "500")),
            Integer.valueOf(System.getenv().getOrDefault("WINDOW_GRID_SIZE", "32")),
            Integer.valueOf(System.getenv().getOrDefault("WINDOW_MIN_TILE_SIZE", "10")), 
            Integer.valueOf(System.getenv().getOrDefault("WINDOW_MAX_TILE_SIZE", "10"))
        );
    }

}