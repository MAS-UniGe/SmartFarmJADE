package com.sdai.smartfarm.agents;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.utils.Position;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class BaseFarmingAgent extends Agent {
    
    protected int x;
    protected int y;
    protected transient ObservableEnvironment environment;
    protected transient ObservedEnvironment observedEnvironment;

    protected int[] fieldsMap;
    protected transient List<List<Position>> fields;

    protected transient Map<AgentType, AID[]> knownAgents = new EnumMap<>(AgentType.class);

    public abstract AgentType getType();

    protected void registerToYellowPages() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(getType().toString());
        sd.setName("Smart-Farm-Simulation");
        
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ObservableEnvironment getEnvironment() {
        return environment;
    }

    public ObservedEnvironment getObservedEnvironment() {
        return observedEnvironment;
    }

    public void situate(Environment environment, Integer x, Integer y) {
        if(environment == null) 
            throw new IllegalArgumentException("situate: environment must be instantiated");

        this.observedEnvironment = new ObservedEnvironment(
            environment.getMap(), 
            environment.getWidth(), 
            environment.getHeight()
        );

        this.environment = environment; // From now on it's just an "ObservableEnvironment" -> a.k.a. we're almost done cheating

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

    public int[] getFieldsMap() {
        return fieldsMap;
    }
    public void setFieldsMap(int[] fieldsMap) {
        this.fieldsMap = fieldsMap;
    }

    public List<List<Position>> getFields() {
        return fields;
    }
    public void setFields(List<List<Position>> fields) {
        this.fields = fields;
    }

    public AID[] getKnown(AgentType agentType) {
        return knownAgents.get(agentType);
    }
    public void setKnown(AgentType agentType, AID[] aids) {
        knownAgents.put(agentType, aids);
    }


}
