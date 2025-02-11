package com.sdai.smartfarm.agents;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.environment.ObservableEnvironment;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.models.FarmField;
import com.sdai.smartfarm.models.Position;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public abstract class BaseFarmingAgent extends Agent {
    
    protected Position position;
    protected transient ObservableEnvironment environment;
    protected transient ObservedEnvironment observedEnvironment;

    protected int[] fieldsMap;
    protected transient List<FarmField> fields;

    protected transient Set<Integer> fieldsToAvoid = new HashSet<>();

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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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

        this.position = new Position(x.intValue(), y.intValue());

    }

    public int[] getFieldsMap() {
        return fieldsMap;
    }
    public void setFieldsMap(int[] fieldsMap) {
        this.fieldsMap = fieldsMap;
    }

    public List<FarmField> getFields() {
        return fields;
    }
    public void setFields(List<FarmField> fields) {
        this.fields = fields;
    }

    public Set<Integer> getFieldsToAvoid() {
        return fieldsToAvoid;
    }
    public void avoidField(int field) {
        fieldsToAvoid.add(field);
    }
    public void allowField(int field) {
        fieldsToAvoid.remove(field);
    }

    public AID[] getKnown(AgentType agentType) {
        return knownAgents.get(agentType);
    }
    public void setKnown(AgentType agentType, AID[] aids) {
        knownAgents.put(agentType, aids);
    }


}
