package com.sdai.smartfarm;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.EnvironmentVisualizer;

public class Main {
    
    public static void main(String[] args) {
        Environment environment = new Environment(50, 50);

        EnvironmentVisualizer environmentVisualizer = new EnvironmentVisualizer(environment);

        environmentVisualizer.renderEnvironment();
    }
}
