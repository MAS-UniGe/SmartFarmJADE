package com.sdai.smartfarm.agents.drone.behaviours;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.sdai.smartfarm.Main;
import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.models.Position;
import com.sdai.smartfarm.settings.SimulationSettings;
import com.sdai.smartfarm.utils.BalancedKMeans;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;


/**
 *  This behaviour is going to be assigned to only one drone at a time and only when the drone configuration changes:
 *  it is the behaviour that the Master Drone uses to balance the workload among its peers
 */
public class LoadBalancingBehaviour extends OneShotBehaviour {

    protected static SimulationSettings simulationSettings = SimulationSettings.defaultSimulationSettings();

    private static final Logger LOGGER = Logger.getLogger(LoadBalancingBehaviour.class.getName());

    @Override
    public void action() {

        LOGGER.info("The Master Drone is deciding the Tiles assignment");

        DroneAgent agent = (DroneAgent) getAgent(); // Only a DroneAgent can have the MasterDroneInitBehaviour

        AID[] receivers = agent.getKnown(AgentType.DRONE);

        List<List<Position>> clusters = BalancedKMeans.clusterize(agent.getFields(), receivers.length);

        try {
            for(int i = 0; i < receivers.length; i++) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(receivers[i]);
                msg.setConversationId("Region-Assignment");
                msg.setContentObject((Serializable) clusters.get(i));
                agent.send(msg);
            }

        } catch(IOException e) {
            LOGGER.severe("cluster was not serializable...");
            throw new IllegalStateException();
        }

        LOGGER.info("The Master Drone has sent the Tiles assignment");
        
    }

}

