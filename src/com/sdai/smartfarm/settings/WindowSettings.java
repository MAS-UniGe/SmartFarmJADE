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
            500,
            500,
            32,
            40, 
            120
        );
    }

}
