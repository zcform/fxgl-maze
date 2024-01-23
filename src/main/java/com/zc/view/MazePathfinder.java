package com.zc.view;

import com.almasb.fxgl.pathfinding.Pathfinder;
import com.almasb.fxgl.pathfinding.astar.AStarCell;
import com.almasb.fxgl.pathfinding.maze.Maze;
import com.almasb.fxgl.pathfinding.maze.MazeCell;

import java.util.*;

public final class MazePathfinder implements Pathfinder<MazeCell> {

    private final Maze maze;

    public MazePathfinder(Maze maze) {
        this.maze = maze;
    }

    @Override
    public List<MazeCell> findPath(int sourceX, int sourceY, int targetX, int targetY) {
        return findPath(maze.get(sourceX, sourceY), maze.get(targetX, targetY));
    }

    @Override
    public List<MazeCell> findPath(int sourceX, int sourceY, int targetX, int targetY, List<MazeCell> busyCells) {
        return findPath(maze.get(sourceX, sourceY), maze.get(targetX, targetY), busyCells.toArray(new MazeCell[0]));
    }

    public List<MazeCell> findPath(MazeCell start, MazeCell target, MazeCell... busyNodes) {
        Set<MazeCell> open = new HashSet<>();
        Set<MazeCell> closed = new HashSet<>();

        MazeCell current = start;

        boolean found = false;

        while (!found && !closed.contains(target)) {
            for (MazeCell neighbor : getValidNeighbors(current, busyNodes)) {
                if (neighbor == target) {
                    target.setUserData(current);
                    found = true;
                    closed.add(target);
                    break;
                }

                if (!closed.contains(neighbor) && !open.contains(neighbor)) {
                    neighbor.setUserData(current);
                    open.add(neighbor);
                }
            }

            if (!found) {
                closed.add(current);
                open.remove(current);

                if (open.isEmpty())
                    return Collections.emptyList();

                current = open.iterator().next();
            }
        }

        return new ArrayList<>(buildPath(start, target));
    }

    private List<MazeCell> buildPath(MazeCell start, MazeCell target) {
        List<MazeCell> path = new ArrayList<>();

        MazeCell tmp = target;
        do {
            path.add(tmp);
            tmp = (MazeCell) tmp.getUserData();
        } while (tmp != start);

        Collections.reverse(path);
        return path;
    }

    public List<MazeCell> getValidNeighbors(MazeCell node, MazeCell... busyNodes) {
        List<MazeCell> result = new ArrayList<>();

        if (!node.hasLeftWall()) {
            maze.getLeft(node).ifPresent(result::add);
        }

        if (!node.hasTopWall()) {
            maze.getUp(node).ifPresent(result::add);
        }

        maze.getRight(node).ifPresent(n -> {
            if (!n.hasLeftWall()) result.add(n);
        });

        maze.getDown(node).ifPresent(n -> {
            if (!n.hasTopWall()) result.add(n);
        });

        result.removeAll(Arrays.asList(busyNodes));

        return result;
    }

    public List<MazeCell> getValidNeighbors(int x, int y, MazeCell... busyNodes) {
        return getValidNeighbors(maze.get(x, y), busyNodes);
    }
}
