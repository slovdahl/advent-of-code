package lib;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkState;

public class Dijkstra<T extends Dijkstra.MatrixType> {

    private final T matrix;
    private final Coordinate start;
    private final Coordinate target;
    private final QuadFunction<T, @Nullable Direction, Coordinate, Coordinate, Integer> costFunction;
    private final Map<Coordinate, Node> unvisited;
    private final Map<Coordinate, Node> visitedNodes;

    public Dijkstra(T matrix, Coordinate start, Coordinate target, QuadFunction<T, @Nullable Direction, Coordinate, Coordinate, Integer> costFunction) {
        checkState(start.in(matrix.toCharArray()), "start outside matrix");
        checkState(target.in(matrix.toCharArray()), "target outside matrix");

        this.matrix = matrix;
        this.start = start;
        this.target = target;
        this.costFunction = costFunction;

        unvisited = new HashMap<>();
        visitedNodes = new HashMap<>();
    }

    /** @return the cost to reach the target */
    public int traverse() {
        unvisited.put(start, new Node(start, null, null, 0));

        while (!unvisited.isEmpty()) {
            // Find the node with the current lowest cost
            Node current = unvisited.values()
                    .stream()
                    .min(Node::compareTo)
                    .orElseThrow();
            unvisited.remove(current.coordinate);

            // Mark the current node visited
            visitedNodes.put(current.coordinate, current);

            for (Direction direction : Direction.ALL) {
                // Check if we can or need to move in this direction
                Coordinate next = matrix.tryMove(current.coordinate, direction);
                if (next == null || visitedNodes.containsKey(next)) {
                    continue;
                }

                Node from = current.inFrom.get(direction);
                if (from == null && !current.inFrom.isEmpty()) {
                    from = current.inFrom.values().stream().findFirst().orElseThrow();
                }

                // Figure out which way we got to the current coordinate
                Direction inDirection;
                if (from != null) {
                    inDirection = from.coordinate.directionTo(current.coordinate);
                } else {
                    inDirection = null;
                }

                Integer cost = costFunction.apply(matrix, inDirection, current.coordinate, next);
                int totalCost = current.cost + cost;

                unvisited.compute(next, (n, node) -> {
                    if (node == null) {
                        return new Node(n, current, direction, totalCost);
                    } else {
                        node.updateCost(current, direction, totalCost);
                        return node;
                    }
                });
            }
        }

        return visitedNodes.get(target).cost;
    }

    public void visualize(char pathCharacter) {
        char[][] m = matrix.toCharArray();

        Node current = visitedNodes.get(target);
        while (current != null) {
            m[current.coordinate.row()][current.coordinate.column()] = pathCharacter;
            Iterator<Map.Entry<Direction, Node>> iterator = current.inFrom.entrySet().iterator();
            if (iterator.hasNext()) {
                current = iterator.next().getValue();
            } else {
                current = null;
            }
        }

        Matrix.print(System.out, m);
    }

    public static class Node implements Comparable<Node> {

        private final Coordinate coordinate;
        private final Map<Direction, Node> inFrom;
        private int cost;

        Node(Coordinate coordinate, Node from, Direction inDirection, int cost) {
            this.coordinate = coordinate;
            this.cost = cost;

            inFrom = new EnumMap<>(Direction.class);
            if (from != null && inDirection != null) {
                inFrom.put(inDirection, from);
            }
        }

        void updateCost(Node from, Direction inDirection, int cost) {
            if (cost < this.cost) {
                this.cost = cost;
                inFrom.clear();
                inFrom.put(inDirection, from);
            } else if (cost == this.cost) {
                inFrom.put(inDirection, from);
            }
        }

        @Override
        public int compareTo(Node o) {
            return Integer.compare(cost, o.cost);
        }

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

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("coordinate", coordinate)
                    .add("inFrom", inFrom.keySet())
                    .add("cost", cost)
                    .toString();
        }
    }

    public sealed interface MatrixType permits CharMatrix, IntMatrix {

        @Nullable
        Coordinate tryMove(Coordinate coordinate, Direction direction);

        int rows();

        int columns();

        char[][] toCharArray();
    }

    public static final class CharMatrix implements MatrixType {
        private final char[][] matrix;
        private final Function<Coordinate, Boolean> validMoveFunction;

        public CharMatrix(char[][] matrix, Function<Coordinate, Boolean> validMoveFunction) {
            this.matrix = matrix;
            this.validMoveFunction = validMoveFunction;
        }

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
        public char[][] toCharArray() {
            return Matrix.deepClone(matrix);
        }
    }

    public static final class IntMatrix implements MatrixType {
        private final int[][] matrix;
        private final Function<Coordinate, Boolean> validMoveFunction;

        public IntMatrix(int[][] matrix, Function<Coordinate, Boolean> validMoveFunction) {
            this.matrix = matrix;
            this.validMoveFunction = validMoveFunction;
        }

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
        public char[][] toCharArray() {
            return Matrix.toCharArray(matrix);
        }
    }
}
