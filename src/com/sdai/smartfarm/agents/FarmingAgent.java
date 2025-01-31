package com.sdai.smartfarm.agents;

import java.util.List;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.ObservableEnvironment;

import elki.data.IntegerVector;

public interface FarmingAgent {
    
    AgentType getType();

    int getX();
    void setX(int x);

    int getY();
    void setY(int y);

    ObservableEnvironment getEnvironment();

    void situate(Environment environment, Integer x, Integer y);

    int[] getFieldsMap();
    void setFieldsMap(int[] fieldsMap);

    List<List<IntegerVector>> getFields();
    void setFields(List<List<IntegerVector>> fields);

}
