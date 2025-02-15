package com.sdai.smartfarm.utils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.sdai.smartfarm.agents.AgentType;
import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;
import com.sdai.smartfarm.models.Position;

public class AStar {

    private AStar() {}

    // this function could be collapsed into returning a single boolean computation
    // but it would become unreadable
    private static boolean shouldMoveThere(
        Position position,
        Position nextPosition,
        AgentType agentType,
        ObservedEnvironment environment,
        int[] fieldsMap,
        Set<Integer> fieldsToAvoid
    ) {
        // Can't go out of bounds
        if (
            nextPosition.x() < 0 
            || nextPosition.x() >= environment.width() 
            || nextPosition.y() < 0 
            || nextPosition.y() >= environment.height()
        ) 
            return false;

        int index0 = position.y() * environment.width() + position.x();
        int index1 = nextPosition.y() * environment.width() + nextPosition.x();

        TileType posTile = environment.map()[index0];
        TileType nextPosTile = environment.map()[index1];

        if (
            agentType == AgentType.ROBOT
            && posTile == TileType.FARMLAND 
            && nextPosTile == TileType.FARMLAND
            && position.y() != nextPosition.y()
        ) {
            // A robot can move vertically in a farm only to dodge obstacles
            boolean hasAnAlternative = true;
            if (position.x() > 0) {
                int index2 = index0 - 1;
                TileType alternative = environment.map()[index2];
                if (alternative == TileType.OBSTACLE || alternative == TileType.TALL_OBSTACLE)
                    hasAnAlternative = false;
            }
            if (position.x() < environment.width() - 1) {
                int index2 = index0 + 1;
                TileType alternative = environment.map()[index2];
                if (alternative == TileType.OBSTACLE || alternative == TileType.TALL_OBSTACLE)
                    hasAnAlternative = false;
            }
            if(hasAnAlternative)
                return false;
                
        }


        // Cannot avoid tall obstacles and cannot fly over short ones if not drone
        if (
            nextPosTile == TileType.TALL_OBSTACLE
            || (nextPosTile == TileType.OBSTACLE && agentType != AgentType.DRONE)
        )
            return false;

        int field = fieldsMap[index0];
        int nextField = fieldsMap[index1];

        // check if the field is in the fields to avoid (and if it is block the movement unless we're trying to move outside from it)
        return (
            fieldsToAvoid == null 
            || field == nextField 
            || !fieldsToAvoid.contains(nextField)
        );

    }

    // returns null on impossible path
    // start is omitted
    public static List<Position> reachSingleDestination(
        Position startPosition,
        Position destPosition,
        ObservedEnvironment observedEnvironment,
        AgentType agentType,
        int[] fieldsMap,
        Set<Integer> fieldsToAvoid
    ) {
            
        if(!shouldMoveThere(
            destPosition, // For this check we don't care about the start position so I set it == to destPosition
            destPosition, 
            agentType, 
            observedEnvironment, 
            fieldsMap, 
            fieldsToAvoid
        )) return null;

        Set<Position> visited = new HashSet<>(); // We cold use a simple 2D array, but its allocation cost would be an overhead I am not willing to take 
        
        PriorityQueue<DecoratedPosition> positionsQueue = new PriorityQueue<>(
            Comparator.comparingInt(DecoratedPosition::getTotalCost)
        );

        positionsQueue.add(new DecoratedPosition(startPosition, 0, startPosition.distance(destPosition), null));

        while(!positionsQueue.isEmpty()) {

            DecoratedPosition decoratedPosition = positionsQueue.poll();
            Position position = decoratedPosition.position();

            if (visited.contains(position)) 
                continue;

            if (position.equals(destPosition)) {
                // then we've found the path
                LinkedList<Position> path = new LinkedList<>();
                
                path.addFirst(destPosition);
                decoratedPosition = decoratedPosition.parent();
                while (decoratedPosition != null && decoratedPosition.parent() != null) {
                    path.addFirst(decoratedPosition.position());
                    decoratedPosition = decoratedPosition.parent();
                }

                return path;
            }

            visited.add(position);

            // extract neighbors
            int[] xOffsets = {1, 0, -1, 0};
            int[] yOffsets = {0, 1, 0, -1};
            for (int i = 0; i < 4; i++) {

                Position neighborPosition = new Position(position.x() + xOffsets[i], position.y() + yOffsets[i]);

                if (!shouldMoveThere(
                    position, 
                    neighborPosition, 
                    agentType, 
                    observedEnvironment, 
                    fieldsMap, 
                    fieldsToAvoid
                ))
                    continue;
                

                DecoratedPosition decoratedNeighborPosition = new DecoratedPosition(
                    neighborPosition, 
                    decoratedPosition.reachCost() + 1,
                    neighborPosition.distance(destPosition),
                    decoratedPosition
                );

                positionsQueue.add(decoratedNeighborPosition);
                
            }

        }

        return null;
        
    }
    
}

record DecoratedPosition(
    Position position,
    int reachCost,
    int heuristicCost,
    DecoratedPosition parent
) {

    public int getTotalCost() {
        return reachCost + heuristicCost;
    }

}
