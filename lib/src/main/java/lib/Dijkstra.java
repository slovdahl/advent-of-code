package lib;

import com.google.common.base.MoreObjects;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

public class Dijkstra<T extends Dijkstra.MatrixType> {

    private final T matrix;
    private final Coordinate start;
    private final Coordinate target;
    private final QuadFunction<T, @Nullable Direction, Coordinate, Coordinate, Integer> costFunction;
    private final Map<Coordinate, Node> unvisited;
    private final Map<Coordinate, Node> visitedNodes;

    public Dijkstra(T matrix,
                    Coordinate start,
                    Coordinate target) {

        this(
                matrix,
                start,
                target,
                (_, _, _, _) -> 1
        );
    }

    public Dijkstra(T matrix,
                    Coordinate start,
                    Coordinate target,
                    QuadFunction<T, @Nullable Direction, Coordinate, Coordinate, Integer> costFunction) {

        checkState(matrix.contains(start), "start outside matrix");
        checkState(matrix.contains(target), "target outside matrix");

        this.matrix = matrix;
        this.start = start;
        this.target = target;
        this.costFunction = costFunction;

        unvisited = new HashMap<>();
        visitedNodes = new HashMap<>();
    }

    /** @return the cost to reach the target, if it's reachable */
    public OptionalInt findShortestPath() {
        return traverse(TraverseMode.BEST_PATH);
    }

    /** @return the cost to reach the target, if it's reachable */
    public OptionalInt findAllShortestPaths() {
        return traverse(TraverseMode.ALL_BEST_PATHS);
    }

    private OptionalInt traverse(TraverseMode mode) {
        unvisited.put(start, Node.start(mode, start));

        while (!unvisited.isEmpty()) {
            // Find the node with the current lowest cost
            Node current = unvisited.values()
                    .stream()
                    .min(Node::compareTo)
                    .orElseThrow();
            unvisited.remove(current.coordinate);

            // Mark the current node visited
            Node result = visitedNodes.put(current.coordinate, current);
            if (result != null && current != result) {
                throw new IllegalStateException("Tried to mark node " + current.coordinate + " as visited, but it was already marked");
            }

            for (Direction direction : Direction.ALL) {
                // Check if we can or need to move in this direction
                Coordinate next = matrix.tryMove(current.coordinate, direction);
                if (next == null || next.equals(start)) {
                    continue;
                }

                Node visited = visitedNodes.get(next);
                if (mode == TraverseMode.BEST_PATH) {
                    if (visited != null) {
                        continue;
                    }
                } else {
                    if (visited != null && visited.previous.containsKey(direction)) {
                        continue;
                    }
                }

                // Find the previous node
                Node from = current.previous.get(direction);
                if (from == null && !current.previous.isEmpty()) {
                    from = current.previous.values().stream().findFirst().orElseThrow();
                }

                // Figure out which way we got to the current coordinate
                Direction inDirection;
                if (from != null) {
                    inDirection = from.coordinate.directionTo(current.coordinate);
                } else {
                    inDirection = null;
                }

                Integer cost = costFunction.apply(matrix, inDirection, current.coordinate, next);
                int totalCost = current.cost(inDirection) + cost;

                unvisited.compute(next, (k, node) -> {
                    Node n = Objects.requireNonNullElseGet(
                            node,
                            () -> visitedNodes.getOrDefault(k, Node.of(k, current, direction))
                    );

                    n.updateCost(current, direction, totalCost);
                    return n;
                });
            }
        }

        Node targetNode = visitedNodes.get(target);
        if (targetNode != null) {
            return targetNode.lowestCost();
        } else {
            return OptionalInt.empty();
        }
    }

    public List<Coordinate> getLowestCostPath() {
        Set<Coordinate> bestPath = new LinkedHashSet<>();

        Node traverseStart = visitedNodes.get(target);
        Node traverseTarget = visitedNodes.get(start);
        traverseLowestCostPath(bestPath, traverseStart, null, traverseTarget);

        if (!bestPath.contains(traverseTarget.coordinate) || !bestPath.contains(traverseStart.coordinate)) {
            throw new IllegalStateException("Failed to traverse the full path");
        }

        List<Coordinate> list = new ArrayList<>(bestPath);
        return List.copyOf(list.reversed());
    }

    private void traverseLowestCostPath(Set<Coordinate> bestPath, Node current, @Nullable Node previous, Node target) {
        bestPath.add(current.coordinate);

        if (current.equals(target)) {
            return;
        }

        Node next = null;

        if (current instanceof BestPathNode) {
            if (previous != null) {
                // Prefer paths that continue in the same direction as far as possible
                next = current.previous.get(previous.coordinate.directionTo(current.coordinate).opposite());
            }

            if (next == null) {
                Iterator<Node> iterator = current.previous.values().iterator();
                if (!iterator.hasNext()) {
                    throw new IllegalStateException();
                }

                next = iterator.next();
            }
        } else {
            next = current.previous.values().stream()
                    .min(Node::compareTo)
                    .orElseThrow();
        }

        if (bestPath.contains(next.coordinate)) {
            return;
        }

        if (!visitedNodes.containsKey(next.coordinate)) {
            return;
        }

        traverseLowestCostPath(bestPath, next, current, target);
    }

    public void visualizeLowestCostPath(char pathCharacter) {
        char[][] m = matrix.toCharArray();

        for (Coordinate coordinate : getLowestCostPath()) {
            m[coordinate.row()][coordinate.column()] = pathCharacter;
        }

        Matrix.print(System.out, m);
    }

    public int[][] costMatrix() {
        int[][] costs = new int[matrix.rows()][matrix.columns()];

        for (Node node : visitedNodes.values()) {
            costs[node.coordinate.row()][node.coordinate.column()] = node.lowestCost().orElse(-1);
        }

        return costs;
    }

    private static abstract sealed class Node implements Comparable<Node> {

        static Node start(TraverseMode mode, Coordinate coordinate) {
            return switch (mode) {
                case BEST_PATH -> new BestPathNode(coordinate, null, null, true);
                case ALL_BEST_PATHS -> new AllPathsNode(coordinate, null, null, true);
            };
        }

        static Node of(Coordinate coordinate, Node from, Direction inDirection) {
            return switch (from) {
                case BestPathNode _ -> new BestPathNode(coordinate, from, inDirection, false);
                case AllPathsNode _ -> new AllPathsNode(coordinate, from, inDirection, false);
            };
        }

        protected final Coordinate coordinate;
        protected final Map<Direction, Node> previous;

        private Node(Coordinate coordinate, @Nullable Node from, @Nullable Direction inDirection) {
            this.coordinate = coordinate;
            previous = new EnumMap<>(Direction.class);

            if (from != null && inDirection != null) {
                previous.put(inDirection, from);
            }
        }

        abstract void updateCost(Node from, Direction inDirection, int cost);

        abstract int cost(@Nullable Direction inDirection);

        abstract OptionalInt lowestCost();

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Node node)) {
                return false;
            }
            return Objects.equals(coordinate, node.coordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate);
        }
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals.
     */
    private static final class BestPathNode extends Node {

        private int cost;

        private BestPathNode(Coordinate coordinate, @Nullable Node from, @Nullable Direction inDirection, boolean startNode) {
            super(coordinate, from, inDirection);

            if (startNode) {
                cost = 0;
            } else {
                cost = Integer.MAX_VALUE;
            }
        }

        @Override
        void updateCost(Node from, Direction inDirection, int cost) {
            if (cost < this.cost) {
                this.cost = cost;
                previous.clear();
                previous.put(inDirection, from);
            } else if (cost == this.cost) {
                previous.put(inDirection, from);
            }
        }

        @Override
        int cost(@Nullable Direction inDirection) {
            return cost;
        }

        @Override
        public OptionalInt lowestCost() {
            return OptionalInt.of(cost);
        }

        @Override
        public int compareTo(Node o) {
            if (!(o instanceof BestPathNode bpn)) {
                throw new IllegalStateException();
            }

            return Integer.compare(
                    cost,
                    bpn.cost
            );
        }


        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("cost", cost)
                    .add("coordinate", coordinate)
                    .add("previous", previous.keySet())
                    .toString();
        }
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals.
     */
    private static final class AllPathsNode extends Node {

        private final Map<Direction, Integer> cost;

        private AllPathsNode(Coordinate coordinate, @Nullable Node from, @Nullable Direction inDirection, boolean startNode) {
            super(coordinate, from, inDirection);

            cost = new EnumMap<>(Direction.class);

            if (startNode) {
                for (Direction direction : Direction.ALL) {
                    cost.put(direction, 0);
                }
            }
        }

        @Override
        void updateCost(Node from, Direction inDirection, int cost) {
            this.cost.compute(inDirection, (_, oldCost) -> {
                if (oldCost == null || cost < oldCost) {
                    return cost;
                } else {
                    return oldCost;
                }
            });

            previous.put(inDirection, from);
        }

        @Override
        int cost(@Nullable Direction inDirection) {
            Integer currentCost;
            if (inDirection == null) {
                // We're most likely trying to get the cost for the start node
                currentCost = cost.get(Direction.UP);
            } else {
                currentCost = cost.get(inDirection);
            }

            if (currentCost == null) {
                throw new IllegalStateException();
            }

            return currentCost;
        }

        @Override
        public OptionalInt lowestCost() {
            return cost
                    .values()
                    .stream()
                    .mapToInt(Integer::intValue)
                    .min();
        }

        @Override
        public int compareTo(Node o) {
            if (!(o instanceof AllPathsNode apn)) {
                throw new IllegalStateException();
            }

            return Integer.compare(
                    lowestCost().orElse(Integer.MAX_VALUE),
                    apn.lowestCost().orElse(Integer.MAX_VALUE)
            );
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("cost", cost)
                    .add("coordinate", coordinate)
                    .add("previous", previous.keySet())
                    .toString();
        }
    }

    public sealed interface MatrixType permits CharMatrix, IntMatrix {

        @Nullable
        Coordinate tryMove(Coordinate coordinate, Direction direction);

        int rows();

        int columns();

        boolean contains(Coordinate coordinate);

        char[][] toCharArray();
    }

    public record CharMatrix(char[][] matrix, Function<Coordinate, Boolean> validMoveFunction) implements MatrixType {

        @Override
        @Nullable
        public Coordinate tryMove(Coordinate current, Direction direction) {
            Coordinate next = current.tryMove(matrix, direction);
            if (next != null && validMoveFunction.apply(next)) {
                return next;
            } else {
                return null;
            }
        }

        @Override
        public int rows() {
            return matrix.length;
        }

        @Override
        public int columns() {
            return matrix[0].length;
        }

        @Override
        public boolean contains(Coordinate coordinate) {
            return coordinate.in(matrix);
        }

        @Override
        public char[][] toCharArray() {
            return Matrix.deepClone(matrix);
        }
    }

    public record IntMatrix(int[][] matrix, Function<Coordinate, Boolean> validMoveFunction) implements MatrixType {

        @Override
        public Coordinate tryMove(Coordinate current, Direction direction) {
            Coordinate next = current.tryMove(matrix, direction);
            if (validMoveFunction.apply(next)) {
                return next;
            } else {
                return null;
            }
        }

        @Override
        public int rows() {
            return matrix.length;
        }

        @Override
        public int columns() {
            return matrix[0].length;
        }

        @Override
        public boolean contains(Coordinate coordinate) {
            return coordinate.in(matrix);
        }

        @Override
        public char[][] toCharArray() {
            return Matrix.toCharArray(matrix);
        }
    }

    private enum TraverseMode {
        BEST_PATH,
        ALL_BEST_PATHS
    }
}
