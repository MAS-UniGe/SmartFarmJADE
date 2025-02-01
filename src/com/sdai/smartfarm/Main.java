package com.sdai.smartfarm;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.DroneAgent;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.EnvironmentController;
import com.sdai.smartfarm.settings.SimulationSettings;

public class Main {

    public static final Handler HANDLER = new ConsoleHandler();

    static {
        HANDLER.setLevel(Level.INFO);
        restoreLogging();
    }

    // this is needed because of Elki :/
    public static void restoreLogging() {

        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
    
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
    
        rootLogger.addHandler(HANDLER);
    }

    
    public static void main(String[] args) {

        SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();

        Environment environment = new Environment(
            simulationSettings.mapWidth(), 
            simulationSettings.mapHeight()
        );

        EnvironmentController environmentController = new EnvironmentController(environment);

        environmentController.init();

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        ContainerController mainContainer = runtime.createMainContainer(profile);

        try {

            for(int i = 0; i < simulationSettings.dronesNumber(); i++) {

                AgentController agent = mainContainer.createNewAgent(
                    "Drone-" + DroneAgent.getInstanceNumber(),
                    DroneAgent.class.getName(),
                    new Object[] {environment} 
                );
                agent.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        environmentController.start();
    }
}
