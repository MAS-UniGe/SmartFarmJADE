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

import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.agents.tractor.TractorAgent;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.EnvironmentController;
import com.sdai.smartfarm.settings.SimulationSettings;

public class Main {

    public static final Handler HANDLER = new ConsoleHandler();

    private static double totalReward = 0.0;

    static {
        HANDLER.setLevel(Level.INFO);
        restoreLogging();
    }

    // this is needed because of Elki :/
    public static void restoreLogging() {

        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO); // change to fine for more logs (be careful that Elki prints a lot of trash)
    
        for (Handler h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
    
        rootLogger.addHandler(HANDLER);
    }

    public static double getTotalReward() {
        return totalReward;
    }

    public static synchronized void addTotalReward(double newReward) {
        totalReward += newReward;
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

            for(int i = 0; i < simulationSettings.robotsNumber(); i++) {

                AgentController agent = mainContainer.createNewAgent(
                    "Robot-" + RobotAgent.getInstanceNumber(),
                    RobotAgent.class.getName(),
                    new Object[] {environment} 
                );
                agent.start();
            }

            for(int i = 0; i < simulationSettings.tractorsNumber(); i++) {

                AgentController agent = mainContainer.createNewAgent(
                    "Tractor-" + TractorAgent.getInstanceNumber(),
                    TractorAgent.class.getName(),
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
