package com.sdai.smartfarm;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.EnvironmentController;
import com.sdai.smartfarm.environment.EnvironmentViewer;

public class Main {
    
    public static void main(String[] args) {
        Environment environment = new Environment(50, 50);

        EnvironmentController environmentController = new EnvironmentController(environment);

        environmentController.init();

    }
}
