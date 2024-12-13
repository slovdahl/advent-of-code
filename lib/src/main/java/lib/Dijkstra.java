package lib;

import com.google.common.base.MoreObjects;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Dijkstra {

    private final int[][] matrix;
    private final Coordinate start;
    private final Coordinate target;
    private final TriFunction<int[][], Coordinate, Coordinate, Integer> costFunction;
    private final Map<Coordinate, Node> unvisited;
    private final Map<Coordinate, Node> visitedNodes;

    public Dijkstra(int[][] matrix, Coordinate start, Coordinate target, TriFunction<int[][], Coordinate, Coordinate, Integer> costFunction) {
        this.matrix = matrix;
        this.start = start;
        this.target = target;
        this.costFunction = costFunction;

        unvisited = new HashMap<>();
        visitedNodes = new HashMap<>();
    }

    public int traverse() {
        unvisited.put(start, new Node(start, null, null, 0));

        while (!unvisited.isEmpty()) {
            // Find the node with the lowest cost
            Node current = unvisited.values()
                    .stream()
                    .min(Node::compareTo)
                    .orElseThrow();
            unvisited.remove(current.coordinate);

            visitedNodes.put(current.coordinate, current);

            for (Direction direction : Direction.ALL) {
                Coordinate next = current.coordinate.tryMove(matrix, direction);
                if (next == null || visitedNodes.containsKey(next)) {
                    continue;
                }

                // TODO: rework this logic? just record IF we could potentially have to back out
                // TODO: then if we get a lower cost to a neighbor, maybe update predecessors?
                Node from = current.inFrom.get(direction);
                if (from != null && current.inFrom.size() == 1) {
                    Node from2 = from.inFrom.get(direction);
                    if (from2 != null && from.inFrom.size() == 1) {
                        Node from3 = from2.inFrom.get(direction);
                        if (from3 != null && from2.inFrom.size() == 1) {
                            continue;
                        } /*else {
                            from2.inFrom.remove(direction);
                        }*/
                    } /*else {
                        from.inFrom.remove(direction);
                    }*/
                }

                Integer cost = costFunction.apply(matrix, current.coordinate, next);
                int totalCost = current.cost + cost;
                Node updatedNode = unvisited.compute(next, (n, node) -> {
                    if (node == null) {
                        return new Node(n, current, direction, totalCost);
                    } else {
                        node.updateCost(current, direction, totalCost);
                        return node;
                    }
                });
                if (updatedNode.cost == totalCost && updatedNode.inFrom.size() > 1) {
                    // TODO: if lower cost, remove the current direction from
                }
            }
        }

        return visitedNodes.get(target).cost;
    }

    public void visualize() {
        char[][] m = Matrix.toCharArray(matrix);

        Node current = visitedNodes.get(target);
        while (current != null) {
            m[current.coordinate.row()][current.coordinate.column()] = '.';
            // TODO: fix?
            Iterator<Map.Entry<Direction, Node>> iterator = current.inFrom.entrySet().iterator();
            if (iterator.hasNext()) {
                current = iterator.next().getValue();
                if (iterator.hasNext()) {
                    throw new IllegalStateException();
                }
            } else {
                current = null;
            }
        }

        Matrix.print(System.out, m);
    }

    public void visualizeCosts() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                Node node = visitedNodes.get(new Coordinate(i, j));
                System.out.printf("%5d", node.cost);
            }
            System.out.println();
        }
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
}
