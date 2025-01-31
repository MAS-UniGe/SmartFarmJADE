package com.sdai.smartfarm;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

import com.sdai.smartfarm.agents.DroneAgent;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.EnvironmentController;
import com.sdai.smartfarm.settings.SimulationSettings;

public class Main {
    
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

                int id = DroneAgent.getInstanceNumber();
                AgentController agent = mainContainer.createNewAgent(
                    "Drone-" + id,
                    DroneAgent.class.getName(),
                    new Object[] {environment, id} 
                );
                agent.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        environmentController.start();
    }
}
