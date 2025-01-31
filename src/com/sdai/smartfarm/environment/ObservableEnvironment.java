package com.sdai.smartfarm.environment;

import com.sdai.smartfarm.environment.tiles.TileType;

public interface ObservableEnvironment {

    public Observation observe(int xCenter, int yCenter, int radius);

    public boolean moveAgent(int xFrom, int yFrom, int xTo, int yTo);
    
    public TileType[] getMap();

    public int getWidth();

    public int getHeight();

}
