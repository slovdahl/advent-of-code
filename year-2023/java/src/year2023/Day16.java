package year2023;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;
import java.util.stream.Stream;

import static year2023.Common.matrix;

@SuppressWarnings("unused")
public class Day16 extends Day {

    @Override
    Integer part1(Stream<String> input) throws IOException {
        char[][] matrix = matrix(input.toList());
        Set<Tile> energizedTiles = new HashSet<>();

        SequencedSet<TileMove> currentTiles = new LinkedHashSet<>();
        currentTiles.add(new TileMove(new Tile(0, 0), Direction.RIGHT));

        while (!currentTiles.isEmpty()) {
            TileMove current = currentTiles.removeFirst();
            energizedTiles.add(current.tile);

            currentTiles.addAll(switch (matrix[current.tile.row][current.tile.column]) {
                case '\\' -> {
                    if (current.direction == Direction.RIGHT && current.canReflect(Direction.DOWN, matrix)) {
                        yield Set.of(current.down());
                    } else if (current.direction == Direction.LEFT && current.canReflect(Direction.UP, matrix)) {
                        yield Set.of(current.up());
                    } else if (current.direction == Direction.UP && current.canReflect(Direction.LEFT, matrix)) {
                        yield Set.of(current.left());
                    } else if (current.direction == Direction.DOWN && current.canReflect(Direction.RIGHT, matrix)) {
                        yield Set.of(current.right());
                    }
                    yield Set.of();
                }
                case '/' -> {
                    if (current.direction == Direction.RIGHT && current.canReflect(Direction.UP, matrix)) {
                        yield Set.of(current.up());
                    } else if (current.direction == Direction.LEFT && current.canReflect(Direction.DOWN, matrix)) {
                        yield Set.of(current.down());
                    } else if (current.direction == Direction.UP && current.canReflect(Direction.RIGHT, matrix)) {
                        yield Set.of(current.right());
                    } else if (current.direction == Direction.DOWN && current.canReflect(Direction.LEFT, matrix)) {
                        yield Set.of(current.left());
                    }
                    yield Set.of();
                }
                case '-' -> {
                    if (current.direction == Direction.RIGHT && current.canReflect(Direction.RIGHT, matrix)) {
                        yield Set.of(current.right());
                    } else if (current.direction == Direction.LEFT && current.canReflect(Direction.LEFT, matrix)) {
                        yield Set.of(current.left());
                    } else if (current.direction == Direction.DOWN || current.direction == Direction.UP) {
                        Set<TileMove> s = new HashSet<>();
                        if (current.canReflect(Direction.LEFT, matrix)) {
                            s.add(current.left());
                        }
                        if (current.canReflect(Direction.RIGHT, matrix)) {
                            s.add(current.right());
                        }
                        yield s;
                    }
                    yield Set.of();
                }
                case '|' -> {
                    if (current.direction == Direction.UP && current.canReflect(Direction.UP, matrix)) {
                        yield Set.of(current.up());
                    } else if (current.direction == Direction.DOWN && current.canReflect(Direction.DOWN, matrix)) {
                        yield Set.of(current.down());
                    } else if (current.direction == Direction.RIGHT || current.direction == Direction.LEFT) {
                        Set<TileMove> s = new HashSet<>();
                        if (current.canReflect(Direction.UP, matrix)) {
                            s.add(current.up());
                        }
                        if (current.canReflect(Direction.DOWN, matrix)) {
                            s.add(current.down());
                        }
                        yield s;
                    }

                    yield Set.of();
                }
                case '.' -> {
                    if (current.canReflect(current.direction, matrix)) {
                        yield Set.of(
                                switch (current.direction) {
                                    case UP -> current.up();
                                    case RIGHT -> current.right();
                                    case DOWN -> current.down();
                                    case LEFT -> current.left();
                                }
                        );
                    }

                    yield Set.of();
                }
                default -> throw new IllegalStateException();
            });
        }

        return energizedTiles.size(); // Your puzzle answer was 7543
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

    record TileMove(Tile tile, Direction direction) {
        boolean canReflect(Direction direction, char[][] matrix) {
            return switch (direction) {
                case UP -> tile.row > 0;
                case RIGHT -> tile.column < matrix[0].length - 1;
                case DOWN -> tile.row < matrix.length - 1;
                case LEFT -> tile.column > 0;
            };
        }

        TileMove up() {
            return new TileMove(tile.up(), Direction.UP);
        }

        TileMove right() {
            return new TileMove(tile.right(), Direction.RIGHT);
        }

        TileMove down() {
            return new TileMove(tile.down(), Direction.DOWN);
        }

        TileMove left() {
            return new TileMove(tile.left(), Direction.LEFT);
        }
    }

    enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
}
