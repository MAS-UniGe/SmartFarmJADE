package com.sdai.smartfarm.agents.drone.behaviours;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.sdai.smartfarm.agents.drone.DroneAgent;
import com.sdai.smartfarm.logic.AStar;
import com.sdai.smartfarm.models.Position;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class PathPlanningBehaviour extends CyclicBehaviour {

    private static final Logger LOGGER = Logger.getLogger(PathPlanningBehaviour.class.getName());

    @Override
    @SuppressWarnings("unchecked")
    public void action() {

        DroneAgent agent = (DroneAgent) getAgent();

        MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.INFORM),
            MessageTemplate.MatchConversationId("Region-Assignment")
        );
        
        ACLMessage msg = agent.receive(mt);

        if (msg == null) {
            block();
            return;
        }

        LOGGER.info(agent.getLocalName() + ": received a message");
         
        try {
            List<Position> assignedRegion = (List<Position>) msg.getContentObject();
            agent.setAssignedTiles(assignedRegion);

            planPath(agent);
            LOGGER.info(agent.getLocalName() + ": determined a path plan");

        } catch(UnreadableException | ClassCastException e) {
            LOGGER.warning("got an unreadable assignment, ignoring...");
        }
        

    }

    //pun intended
    private void planPath(DroneAgent agent) {

        List<Position> assignedTiles = agent.getAssignedTiles();
        
        int[] fieldsMap = agent.getFieldsMap();

        int width = agent.getObservedEnvironment().width();

        // ALGORITHM:
        // split assignedTiles into fields. For each field, sort tiles by Y and then X, with sorting directions alternated
        // (for "even entry" fields, lower Y are prioritized, for "odd" fields, higher Y are prioritized)
        // (for even Y, lower X are prioritized, for odd Y, higher X are prioritized)
        // start from first tile of first field
        // while next tile is adjacent to current tile: go there and append movement to preferredPath
        // else compute best path from current tile to next tile with A* and then append movement to preferredPath
        // when all tiles in a field have been visited, use A* to move to next field's "first" tile
        // repeat until all tiles have been visited
        // use A* to compute path to go back to first tile (this last step is the most inefficient part, but it is relatively a small price to pay)

        SortedMap<Integer, List<Position>> fieldsAssignment = new TreeMap<>(); // needs to be sorted so "close" fields are next to each other

        for(Position position: assignedTiles) {
            int field = fieldsMap[position.y() * width + position.x()];

            fieldsAssignment.computeIfAbsent(field, f -> new ArrayList<>());
            
            fieldsAssignment.get(field).add(position);
        }

        List<Position> preferredPath = new ArrayList<>();

        boolean goingDown = true;
        for(List<Position> positions: fieldsAssignment.values()) {

            // Sort points inside the same field
            final boolean goingDownAux = goingDown;
            positions.sort(
                Comparator.comparingInt((Position p) -> (goingDownAux)? p.y() : -(p.y()))
            .thenComparingInt(p -> (p.y() % 2 == 0) ? p.x() : -p.x()));

            goingDown = !goingDown; // alternate going down and going up

            if(preferredPath.isEmpty()) {
                preferredPath.add(positions.get(0));
                
            } else {
                // attach first element to last of preferred path
                preferredPath.addAll(
                    AStar.reachSingleDestination(
                        preferredPath.get(preferredPath.size() - 1), 
                        positions.get(0),
                        agent.getObservedEnvironment(),
                        agent.getType(),
                        agent.getFieldsMap(),
                        null
                    )
                );
            }

            for(int i = 0; i < positions.size() - 1; i++) {

                Position curPos = positions.get(i);
                Position nextPos = positions.get(i+1);

                if (curPos.isAdjacent(nextPos)) {
                    preferredPath.add(nextPos);
                } else {
                    preferredPath.addAll(
                        AStar.reachSingleDestination(
                            curPos,
                            nextPos,
                            agent.getObservedEnvironment(),
                            agent.getType(),
                            agent.getFieldsMap(),
                        null
                        )
                    );
                }
            }
                
        }

        // Go back to square one (0 actually)
        preferredPath.addAll(
            AStar.reachSingleDestination(
                preferredPath.get(preferredPath.size() -1),
                preferredPath.get(0),
                agent.getObservedEnvironment(),
                agent.getType(),
                agent.getFieldsMap(),
                null
            )
        );

        agent.setPathPlan(preferredPath);

    }
    
}
