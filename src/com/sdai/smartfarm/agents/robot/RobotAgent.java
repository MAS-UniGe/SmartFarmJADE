package com.sdai.smartfarm.agents.robot;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.agents.BaseFarmingAgent;
import com.sdai.smartfarm.agents.robot.behaviours.CompleteTaskBehaviour;
import com.sdai.smartfarm.agents.robot.behaviours.RobotInitBehaviour;
import com.sdai.smartfarm.environment.Environment;
import com.sdai.smartfarm.utils.AssistanceRequest;
import com.sdai.smartfarm.utils.Position;
import com.sdai.smartfarm.utils.Task;

import jade.core.behaviours.Behaviour;

public class RobotAgent extends BaseFarmingAgent {

    /* STATIC STUFF */

    private static final Logger LOGGER = Logger.getLogger(RobotAgent.class.getName());

    private static int instanceCounter = 0;

    public static int getInstanceNumber() {
        return RobotAgent.instanceCounter++;
    }

    ///////////////////

    // checking presence would be faster with a set, but assuming this dude won't get overworked I'm using
    // a queue to maintain insertion order. BTW being able to use a blocking queue would made
    // my job SO MUCH EASIER, but Jade allocates one single thread per agent so I gotta bite the dust

    protected final transient Deque<Task> tasks = new LinkedList<>();

    // I don't need a whole state machine, I just need this info
    protected Behaviour currentState = null;

    @Override
    public AgentType getType() {
        return AgentType.ROBOT;
    }
    
    @Override
    protected void setup() {

        Object[] args = getArguments();
        if (args == null || args.length < 1) 
            throw new IllegalArgumentException("setup: not enough arguments");
        
        Environment environment = (Environment) args[0];

        Integer x = null;
        Integer y = null;

        if (args.length >= 3) {
            x = (int) args[1];
            y = (int) args[2];
        }

        situate(environment, x, y);

        registerToYellowPages();

        addBehaviour(new RobotInitBehaviour());
        
    }    

    public Behaviour getCurrentState() {
        return currentState;
    }

    public void setCurrentState(Behaviour state) {
        this.currentState = state;
    }
    
    public boolean isHandlingRequest(AssistanceRequest assistanceRequest) {
        return tasks.contains(new Task(assistanceRequest, null, null, null));
    }

    public void assignTask(Task task) {
        tasks.add(task);
    }

    public int getTasksSize() {
        return tasks.size();
    }

    public Task pollTask() {
        return tasks.poll();
    }

    public Position getLastTaskDestination() {

        if (!tasks.isEmpty()) {
            return tasks.getLast().request().position();
        }

        if (currentState instanceof CompleteTaskBehaviour taskBehaviour) {
            return taskBehaviour.getTask().request().position();
        }
        
        return position;

    }

    // this function is vulnerable to race conditions, but it is pointless to make it synchronized: 
    // it can be problematic if the same robot is assigned 2 tasks at the same time, as the estimate 
    // is going to be far off, but it should be a tolerable loss
    public int estimateCostForRequest(AssistanceRequest assistanceRequest) {

        int currentCost = tasks.stream().mapToInt(t -> t.path().size()).sum();

        Position finalPosition = getLastTaskDestination();

        return currentCost + finalPosition.distance(assistanceRequest.position());
    }

}
