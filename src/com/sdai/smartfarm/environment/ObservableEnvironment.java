package com.sdai.smartfarm.environment;

public interface ObservableEnvironment {

    public Observation observe(int xCenter, int yCenter, int radius);

    public boolean moveAgent(int xFrom, int yFrom, int xTo, int yTo);
    
}
