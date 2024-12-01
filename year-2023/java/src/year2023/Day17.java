package year2023;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import lib.Day;
import lib.Direction;

import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;
import static lib.Matrix.intMatrix;
import static lib.Matrix.manhattanDistance;

@SuppressWarnings("unused")
public class Day17 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Integer part1(Stream<String> input) throws IOException {
        Stream<String> sampleInput = """
                2413432311323
                3215453535623
                3255245654254
                3446585845452
                4546657867536
                1438598798454
                4457876987766
                3637877979653
                4654967986887
                4564679986453
                1224686865563
                2546548887735
                4322674655533
                """.lines();

        int[][] matrix = intMatrix(input.toList());

        int goalRow = matrix.length;
        int goalColumn = matrix[0].length;

        PriorityQueue<TileMove> paths = new PriorityQueue<>((m1, m2) -> {
            if (m1.equals(m2)) {
                return 0;
            }

            int distance1 = manhattanDistance(m1.tile.row, m1.tile.column, goalRow, goalColumn);
            int distance2 = manhattanDistance(m2.tile.row, m2.tile.column, goalRow, goalColumn);

            if (distance1 < distance2) {
                return -1;
            } else if (distance2 < distance1) {
                return 1;
            }

            return Integer.compare(m1.incurredHeatLoss.value, m2.incurredHeatLoss.value);
        });

        paths.add(new TileMove(
                new Tile(0, 0),
                Direction.RIGHT,
                new HeatLoss(-matrix[0][0]),
                0,
                new HashSet<>()
        ));

        Multimap<Integer, TileMove> finalStates = HashMultimap.create();
        int lowestHeatLossSeen = Integer.MAX_VALUE;
        //int lowestHeatLossSeen = 1253;
        int iterations = 0;

        while (!paths.isEmpty()) {
            iterations++;

            TileMove current = paths.poll();
            current.incurredHeatLoss.add(current.value(matrix));

            if (current.incurredHeatLoss.value >= lowestHeatLossSeen) {
                continue;
            }

            if (iterations % 200_000 == 0) {
                System.out.println("Current lowest: " + lowestHeatLossSeen);
                final int currentLowestHeatLossSeen = lowestHeatLossSeen;
                paths.removeIf(path -> {
                    if (path.incurredHeatLoss.value >= currentLowestHeatLossSeen) {
                        return true;
                    }

                    int distanceToGoal = manhattanDistance(path.tile.row, path.tile.column, goalRow, goalColumn);
                    if (path.incurredHeatLoss.value + distanceToGoal >= currentLowestHeatLossSeen) {
                        return true;
                    } else if (distanceToGoal > 10 && path.incurredHeatLoss.value + distanceToGoal * 2 >= currentLowestHeatLossSeen) {
                        return true;
                    }

                    return false;
                });
            }

            if (current.tile.row == matrix.length - 1 && current.tile.column == matrix[0].length - 1) {
                finalStates.put(current.incurredHeatLoss.value, current);

                lowestHeatLossSeen = Math.min(
                        current.incurredHeatLoss.value,
                        lowestHeatLossSeen
                );

                continue;
            }

            SortedSetMultimap<Integer, Direction> heatLossPerDirection = TreeMultimap.create(naturalOrder(), naturalOrder());
            for (Direction direction : current.alternatives(matrix)) {
                heatLossPerDirection.put(current.move(direction, matrix), direction);
                /*paths.add(switch (direction) {
                    case UP -> current.up();
                    case RIGHT -> current.right();
                    case DOWN -> current.down();
                    case LEFT -> current.left();
                });*/
            }

            if (heatLossPerDirection.isEmpty()) {
                continue;
            }

            Collection<Direction> nextDirections = heatLossPerDirection.asMap().values().iterator().next();
            for (Direction nextDirection : nextDirections) {
                paths.add(switch (nextDirection) {
                    case UP -> current.up();
                    case RIGHT -> current.right();
                    case DOWN -> current.down();
                    case LEFT -> current.left();
                });
            }
        }

        return finalStates.keySet().stream()
                .mapToInt(v -> v)
                .min()
                .orElseThrow();
        // 1255 too high
        // 1253 too high
    }

    record Tile(int row, int column) {
        Tile up() {
            return new Tile(row - 1, column);
        }

        Tile right() {
            return new Tile(row, column + 1);
        }

        Tile down() {
            return new Tile(row + 1, column);
        }

        Tile left() {
            return new Tile(row, column - 1);
        }
    }

    record TileMove(Tile tile, Direction direction, HeatLoss incurredHeatLoss, int sameDirection, Set<Tile> seenTiles) {
        TileMove up() {
            Set<Tile> seenTiles = new HashSet<>(this.seenTiles);
            seenTiles.add(tile);
            return new TileMove(tile.up(), Direction.UP, incurredHeatLoss.copy(), direction == Direction.UP ? sameDirection + 1 : 1, seenTiles);
        }

        TileMove right() {
            Set<Tile> seenTiles = new HashSet<>(this.seenTiles);
            seenTiles.add(tile);
            return new TileMove(tile.right(), Direction.RIGHT, incurredHeatLoss.copy(), direction == Direction.RIGHT ? sameDirection + 1 : 1, seenTiles);
        }

        TileMove down() {
            Set<Tile> seenTiles = new HashSet<>(this.seenTiles);
            seenTiles.add(tile);
            return new TileMove(tile.down(), Direction.DOWN, incurredHeatLoss.copy(), direction == Direction.DOWN ? sameDirection + 1 : 1, seenTiles);
        }

        TileMove left() {
            Set<Tile> seenTiles = new HashSet<>(this.seenTiles);
            seenTiles.add(tile);
            return new TileMove(tile.left(), Direction.LEFT, incurredHeatLoss.copy(), direction == Direction.LEFT ? sameDirection + 1 : 1, seenTiles);
        }

        int value(int[][] matrix) {
            return matrix[tile.row][tile.column];
        }

        Set<Direction> alternatives(int[][] matrix) {
            return switch (direction) {
                case UP -> {
                    Set<Direction> directions = EnumSet.noneOf(Direction.class);
                    if (tile.row > 0 && sameDirection < 3 && !seenTiles.contains(new Tile(tile.row - 1, tile.column))) {
                        directions.add(Direction.UP);
                    }
                    if (tile.column > 0 && !seenTiles.contains(new Tile(tile.row, tile.column - 1))) {
                        directions.add(Direction.LEFT);
                    }
                    if (tile.column < matrix[0].length - 1 && !seenTiles.contains(new Tile(tile.row, tile.column + 1))) {
                        directions.add(Direction.RIGHT);
                    }
                    yield directions;
                }
                case RIGHT -> {
                    Set<Direction> directions = EnumSet.noneOf(Direction.class);
                    if (tile.row > 0 && !seenTiles.contains(new Tile(tile.row - 1, tile.column))) {
                        directions.add(Direction.UP);
                    }
                    if (tile.row < matrix.length - 1 && !seenTiles.contains(new Tile(tile.row + 1, tile.column))) {
                        directions.add(Direction.DOWN);
                    }
                    if (tile.column < matrix[0].length - 1 && sameDirection < 3 && !seenTiles.contains(new Tile(tile.row, tile.column + 1))) {
                        directions.add(Direction.RIGHT);
                    }
                    yield directions;
                }
                case DOWN -> {
                    Set<Direction> directions = EnumSet.noneOf(Direction.class);
                    if (tile.row < matrix.length - 1 && sameDirection < 3 && !seenTiles.contains(new Tile(tile.row + 1, tile.column))) {
                        directions.add(Direction.DOWN);
                    }
                    if (tile.column > 0 && !seenTiles.contains(new Tile(tile.row, tile.column - 1))) {
                        directions.add(Direction.LEFT);
                    }
                    if (tile.column < matrix[0].length - 1 && !seenTiles.contains(new Tile(tile.row, tile.column + 1))) {
                        directions.add(Direction.RIGHT);
                    }
                    yield directions;
                }
                case LEFT -> {
                    Set<Direction> directions = EnumSet.noneOf(Direction.class);
                    if (tile.row > 0 && !seenTiles.contains(new Tile(tile.row - 1, tile.column))) {
                        directions.add(Direction.UP);
                    }
                    if (tile.row < matrix.length - 1 && !seenTiles.contains(new Tile(tile.row + 1, tile.column))) {
                        directions.add(Direction.DOWN);
                    }
                    if (tile.column > 0 && sameDirection < 3 && !seenTiles.contains(new Tile(tile.row, tile.column - 1))) {
                        directions.add(Direction.LEFT);
                    }
                    yield directions;
                }
            };
        }

        int move(Direction direction, int[][] matrix) {
            return switch (direction) {
                case UP -> matrix[tile.row - 1][tile.column];
                case RIGHT -> matrix[tile.row][tile.column + 1];
                case DOWN -> matrix[tile.row + 1][tile.column];
                case LEFT -> matrix[tile.row][tile.column - 1];
            };
        }
    }

    private static class HeatLoss {
        private int value;

        HeatLoss(int value) {
            this.value = value;
        }

        void add(int value) {
            this.value += value;
        }

        HeatLoss copy() {
            return new HeatLoss(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            HeatLoss heatLoss = (HeatLoss) o;
            return value == heatLoss.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return "" + value;
        }
    }
}
