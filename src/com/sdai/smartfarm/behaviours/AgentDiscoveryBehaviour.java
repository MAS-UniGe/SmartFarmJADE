package com.sdai.smartfarm.behaviours;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.utils.ArrayCheck;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.logging.Logger;

public class AgentDiscoveryBehaviour extends TickerBehaviour {

    private static final Logger logger = Logger.getLogger(AgentDiscoveryBehaviour.class.getName());

    public AgentDiscoveryBehaviour(BaseFarmingAgent agent, long period) {
        super(agent, period);
    }

    @Override
    protected void onTick() {

        BaseFarmingAgent agent = (BaseFarmingAgent) getAgent();

        for(AgentType agentType: AgentType.values()) {
            AID[] alreadyKnown = agent.getKnown(agentType);

            if (alreadyKnown == null)
                alreadyKnown = new AID[0];

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(agentType.toString());
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                if (result == null) {
                    logger.warning("obtained null result");
                    continue;
                }

                AID[] discoveredAgents = new AID[result.length];

                for (int i = 0; i < result.length; ++i) {
                    discoveredAgents[i] = result[i].getName();
                }

                agent.setKnown(agentType, discoveredAgents);

                if (!ArrayCheck.areEquivalent(alreadyKnown, discoveredAgents)) {

                    logger.info(agent.getAID().getLocalName() + ": discovered some new agents");

                    if (agentType == AgentType.DRONE && agent.getAID().getLocalName().equals("Drone-0")) { 
                        // NOTE: This is not fault tolerant (in case Drone-0 breaks) -> could be improved with a consensus algo to decide Master Drone
                        agent.addBehaviour(new MasterDroneInitBehaviour());
                    }

                }
            }
            catch (FIPAException fe) {
                logger.severe("encountered FIPAException: " + fe.getLocalizedMessage());
            }
        }

        
    }
    
}
