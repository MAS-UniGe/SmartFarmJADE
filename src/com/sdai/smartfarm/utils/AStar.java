package com.sdai.smartfarm.utils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import com.sdai.smartfarm.environment.ObservedEnvironment;
import com.sdai.smartfarm.environment.tiles.TileType;

public class AStar {

    private AStar() {}

    // returns null on impossible path
    // start is omitted
    public static List<Position> reachSingleDestination(
        Position startPosition,
        Position destPosition,
        ObservedEnvironment observedEnvironment,
        boolean canFly
    ) {

        int width = observedEnvironment.width();
        int height = observedEnvironment.height();
        TileType[] map = observedEnvironment.map();

        if(startPosition.equals(destPosition)) return new LinkedList<>();
        if(destPosition.x() < 0 || destPosition.x() >= width|| destPosition.y() < 0 || destPosition.y() >= height) return null;
        if(map[destPosition.y() * width + destPosition.x()] == TileType.TALL_OBSTACLE || (!canFly && map[destPosition.y() * width + destPosition.x()] == TileType.OBSTACLE)) return null;

        Set<Position> visited = new HashSet<>(); // We cold use a simple 2D array, but its allocation cost would be an overhead I am not willing to take 
        
        PriorityQueue<DecoratedPosition> positionsQueue = new PriorityQueue<>(
            Comparator.comparingInt(DecoratedPosition::getTotalCost)
        );

        positionsQueue.add(new DecoratedPosition(startPosition, 0, startPosition.distance(destPosition), null));

        while(!positionsQueue.isEmpty()) {

            DecoratedPosition decoratedPosition = positionsQueue.poll();
            Position position = decoratedPosition.position();

            if (position.equals(destPosition)) {
                // then we've found the path
                LinkedList<Position> path = new LinkedList<>();
                
                path.addFirst(destPosition);
                decoratedPosition = decoratedPosition.parent();
                while (decoratedPosition.parent() != null) {
                    path.addFirst(decoratedPosition.position());
                    decoratedPosition = decoratedPosition.parent();
                }

                return path;
            }

            if (visited.contains(position)) 
                continue;

            visited.add(position);

            // extract neighbors
            int[] xOffsets = {1, 0, -1, 0};
            int[] yOffsets = {0, 1, 0, -1};
            for (int i = 0; i < 4; i++) {
                
                int x = position.x() + xOffsets[i];
                int y = position.y() + yOffsets[i];

                if (x < 0 || x >= width || y < 0 || y >= height)
                    continue;

                int index = y * width + x;

                if (
                    (i % 2 == 1) 
                    && (map[index] == TileType.FARMLAND) 
                    && (map[position.y() * width + position.x()] == TileType.FARMLAND) 
                    && !canFly
                ) continue; // can't move vertically on Farmland if can't fly

                if (map[index] != TileType.TALL_OBSTACLE && (map[index] != TileType.OBSTACLE || canFly)) {

                    Position neighborPosition = new Position(x, y);

                    DecoratedPosition decoratedNeighborPosition = new DecoratedPosition(
                        neighborPosition, 
                        decoratedPosition.reachCost() + 1,
                        neighborPosition.distance(destPosition),
                        decoratedPosition
                    );

                    positionsQueue.add(decoratedNeighborPosition);
                }
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
