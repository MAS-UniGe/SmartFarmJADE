package com.sdai.smartfarm.agents.robot.behaviours;

import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.robot.RobotAgent;
import com.sdai.smartfarm.common_behaviours.InitBehaviour;
import com.sdai.smartfarm.settings.AgentsSettings;

import jade.core.behaviours.Behaviour;

public class RobotInitBehaviour extends InitBehaviour {

    private static AgentsSettings settings = AgentsSettings.defaultAgentsSettings();

    @Override
    protected void addBehaviours(BaseFarmingAgent agent) {

        super.addBehaviours(agent);
        
        if (!(agent instanceof RobotAgent robotAgent)) throw new ClassCastException("RobotInitBehaviour should be given only to a Robot!");

        robotAgent.addBehaviour(new OfferAssistanceBehaviour());
        robotAgent.addBehaviour(new AcceptTasksBehaviour());
        
        Behaviour behaviour = new IdleBehaviour();
        robotAgent.addBehaviour(behaviour);
        robotAgent.setCurrentState(behaviour);
    }
}
