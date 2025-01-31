package com.sdai.smartfarm.agents;

import java.util.List;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.ObservableEnvironment;

import elki.data.IntegerVector;
import jade.core.Agent;

public abstract class BaseFarmingAgent extends Agent implements FarmingAgent {
    
    protected int x;
    protected int y;
    protected transient ObservableEnvironment environment;

    protected int[] fieldsMap;
    protected transient List<List<IntegerVector>> fields;

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public ObservableEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void situate(Environment environment, Integer x, Integer y) {
        if(environment == null) 
            throw new IllegalArgumentException("situate: environment must be instantiated");

        this.environment = environment;

        boolean spawned = false;
        while(!spawned) {
            
            spawned = environment.trySpawn(this, x, y);
            if (!spawned) {
                x = environment.getRNG().nextInt(environment.getWidth());
                y = environment.getRNG().nextInt(environment.getHeight());
            }
            
        }

        this.x = x.intValue(); // cannot be null
        this.y = y.intValue();     

    }

    @Override
    public int[] getFieldsMap() {
        return fieldsMap;
    }

    @Override
    public void setFieldsMap(int[] fieldsMap) {
        this.fieldsMap = fieldsMap;
    }

    @Override
    public List<List<IntegerVector>> getFields() {
        return fields;
    }

    @Override
    public void setFields(List<List<IntegerVector>> fields) {
        this.fields = fields;
    }

}
