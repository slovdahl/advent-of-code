package year2023;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;

@SuppressWarnings("unused")
public class Day10 extends Day {

    @Override
    Object part1(Stream<String> input) throws IOException {
        List<String> lines = input.toList();

        char[][] matrix = Common.paddedMatrix(lines, '.');

        Coordinate startingPoint = null;
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == 'S') {
                    startingPoint = new Coordinate(row, column);
                }
            }
        }

        assert startingPoint != null;

        Coordinate current = startingPoint;
        Direction previousDirection = null;
        int steps = 0;
        do {
            char ch = matrix[current.row][current.column];

            PipeType pipeType = PipeType.LOOKUP.get(ch);

            steps++;
            if (previousDirection != Direction.SOUTH && pipeType.north.contains(current.north(matrix))) {
                previousDirection = Direction.NORTH;
                current = current.toNorth();
            } else if (previousDirection != Direction.WEST && pipeType.east.contains(current.east(matrix))) {
                previousDirection = Direction.EAST;
                current = current.toEast();
            } else if (previousDirection != Direction.NORTH && pipeType.south.contains(current.south(matrix))) {
                previousDirection = Direction.SOUTH;
                current = current.toSouth();
            } else if (previousDirection != Direction.EAST && pipeType.west.contains(current.west(matrix))) {
                previousDirection = Direction.WEST;
                current = current.toWest();
            } else {
                if (current.north(matrix) == 'S' ||
                        current.east(matrix) == 'S' ||
                        current.south(matrix) == 'S' ||
                        current.west(matrix) == 'S') {
                    break;
                } else {
                    throw new IllegalStateException();
                }
            }
        } while (true);

        return steps / 2; // Your puzzle answer was 6757
    }

    record Coordinate(int row, int column) {
        boolean isConnectedTo(char currentPipe, int toRow, int toColumn) {
            return switch (currentPipe) {
                case '|' -> column == toColumn && (row + 1 == toRow || row - 1 == toRow);
                case '-' -> row == toRow && (column + 1 == toColumn || column - 1 == toColumn);
                case 'L' -> (column == toColumn && row - 1 == toRow) || (row == toRow && column + 1 == toColumn);
                case 'J' -> (column == toColumn && row - 1 == toRow) || (row == toRow && column - 1 == toColumn);
                case '7' -> (row == toRow && column - 1 == toColumn) || (column == toColumn && row + 1 == toRow);
                case 'F' -> (row == toRow && column + 1 == toColumn) || (column == toColumn && row + 1 == toRow);
                default -> false;
            };
        }

        char north(char[][] matrix) {
            return matrix[row - 1][column];
        }

        char east(char[][] matrix) {
            return matrix[row][column + 1];
        }

        char south(char[][] matrix) {
            return matrix[row + 1][column];
        }

        char west(char[][] matrix) {
            return matrix[row][column - 1];
        }

        Coordinate toNorth() {
            return new Coordinate(row - 1, column);
        }

        Coordinate toEast() {
            return new Coordinate(row, column + 1);
        }

        Coordinate toSouth() {
            return new Coordinate(row + 1, column);
        }

        Coordinate toWest() {
            return new Coordinate(row, column - 1);
        }
    }

    enum PipeType {
        // @formatter:off
        S   ('S', Set.of('|', '7', 'F'), Set.of('-', 'J', '7'), Set.of('|', 'L', 'J'), Set.of('-', 'L', 'F')),
        PIPE('|', Set.of('|', '7', 'F'), Set.of(),              Set.of('|', 'L', 'J'), Set.of()),
        DASH('-', Set.of(),              Set.of('-', 'J', '7'), Set.of(),              Set.of('-', 'L', 'F')),
        L   ('L', Set.of('|', '7', 'F'), Set.of('-', 'J', '7'), Set.of(),              Set.of()),
        J   ('J', Set.of('|', '7', 'F'), Set.of(),              Set.of(),              Set.of('-', 'L', 'F')),
        _7  ('7', Set.of(),              Set.of(),              Set.of('|', 'L', 'J'), Set.of('-', 'L', 'F')),
        F   ('F', Set.of(),              Set.of('-', 'J', '7'), Set.of('|', 'L', 'J'), Set.of()),
        // @formatter:on
        ;

        private static final Map<Character, PipeType> LOOKUP = Arrays.stream(values())
                .collect(toUnmodifiableMap(t -> t.ch, t -> t));

        private final char ch;
        private final Set<Character> north;
        private final Set<Character> east;
        private final Set<Character> south;
        private final Set<Character> west;

        PipeType(char ch,
                 Set<Character> north,
                 Set<Character> east,
                 Set<Character> south,
                 Set<Character> canConnectWest) {

            this.ch = ch;
            this.north = north;
            this.east = east;
            this.south = south;
            this.west = canConnectWest;
        }
    }

    enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST;
    }
}
