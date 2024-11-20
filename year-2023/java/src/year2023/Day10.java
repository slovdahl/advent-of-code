package year2023;

import lib.Coordinate;
import lib.Day;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static lib.Common.paddedMatrix;
import static lib.Common.printWithoutPadding;

@SuppressWarnings("unused")
public class Day10 extends Day {

    private char[][] matrix;
    private Coordinate startingPoint;
    private int steps;
    private Set<Coordinate> seenCoordinates;

    @Override
    protected void prepare(Stream<String> input) {
        List<String> lines = input.toList();

        matrix = paddedMatrix(lines, '.');

        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == 'S') {
                    startingPoint = new Coordinate(row, column);
                }
            }
        }

        if (startingPoint == null) {
            throw new IllegalStateException();
        }

        Direction startingPointOut = null;
        Direction startingPointIn = null;

        Coordinate current = startingPoint;
        Direction previousDirection = null;
        seenCoordinates = new HashSet<>();

        do {
            seenCoordinates.add(current);
            char ch = matrix[current.row()][current.column()];

            PipeType pipeType = PipeType.LOOKUP.get(ch);

            steps++;
            if (previousDirection != Direction.SOUTH && pipeType.north.contains(current.north(matrix))) {
                if (previousDirection == null) {
                    startingPointOut = Direction.NORTH;
                }
                previousDirection = Direction.NORTH;
                current = current.toNorth();
            } else if (previousDirection != Direction.WEST && pipeType.east.contains(current.east(matrix))) {
                if (previousDirection == null) {
                    startingPointOut = Direction.EAST;
                }
                previousDirection = Direction.EAST;
                current = current.toEast();
            } else if (previousDirection != Direction.NORTH && pipeType.south.contains(current.south(matrix))) {
                if (previousDirection == null) {
                    startingPointOut = Direction.SOUTH;
                }
                previousDirection = Direction.SOUTH;
                current = current.toSouth();
            } else if (previousDirection != Direction.EAST && pipeType.west.contains(current.west(matrix))) {
                if (previousDirection == null) {
                    startingPointOut = Direction.WEST;
                }
                previousDirection = Direction.WEST;
                current = current.toWest();
            } else {
                if (current.north(matrix) == 'S') {
                    startingPointIn = Direction.NORTH;
                    break;
                } else if (current.east(matrix) == 'S') {
                    startingPointIn = Direction.EAST;
                    break;
                } else if (current.south(matrix) == 'S') {
                    startingPointIn = Direction.SOUTH;
                    break;
                } else if (current.west(matrix) == 'S') {
                    startingPointIn = Direction.WEST;
                    break;
                } else {
                    throw new IllegalStateException();
                }
            }
        } while (true);

        Set<Character> possibleStartCharacters = Arrays.stream(PipeType.values())
                .filter(t -> t != PipeType.S)
                .map(t -> t.ch)
                .collect(toSet());

        switch (startingPointOut) {
            case NORTH -> {
                possibleStartCharacters.remove('-');
                possibleStartCharacters.remove('7');
                possibleStartCharacters.remove('F');
            }
            case EAST -> {
                possibleStartCharacters.remove('|');
                possibleStartCharacters.remove('J');
                possibleStartCharacters.remove('7');
            }
            case SOUTH -> {
                possibleStartCharacters.remove('-');
                possibleStartCharacters.remove('L');
                possibleStartCharacters.remove('J');
            }
            case WEST -> {
                possibleStartCharacters.remove('|');
                possibleStartCharacters.remove('L');
                possibleStartCharacters.remove('F');
            }
        }

        switch (startingPointIn) {
            case NORTH -> {
                possibleStartCharacters.remove('-');
                possibleStartCharacters.remove('L');
                possibleStartCharacters.remove('J');
            }
            case EAST -> {
                possibleStartCharacters.remove('|');
                possibleStartCharacters.remove('L');
                possibleStartCharacters.remove('F');
            }
            case SOUTH -> {
                possibleStartCharacters.remove('-');
                possibleStartCharacters.remove('7');
                possibleStartCharacters.remove('F');
            }
            case WEST -> {
                possibleStartCharacters.remove('|');
                possibleStartCharacters.remove('J');
                possibleStartCharacters.remove('7');
            }
        }

        if (possibleStartCharacters.size() > 1) {
            throw new IllegalStateException();
        }

        matrix[startingPoint.row()][startingPoint.column()] = possibleStartCharacters.iterator().next();
    }

    @Override
    protected Integer part1(Stream<String> input) throws IOException {
        return steps / 2; // Your puzzle answer was 6757
    }

    @Override
    protected Integer part2(Stream<String> input) throws Exception {
        for (int row = 1; row < matrix.length - 1; row++) {
            for (int column = 1; column < matrix[row].length - 1; column++) {
                if (!seenCoordinates.contains(new Coordinate(row, column))) {
                    matrix[row][column] = ' ';
                }
            }
        }

        printWithoutPadding(matrix);

        Map<Integer, Set<Coordinate>> coordinatesPerRow = seenCoordinates.stream()
                .collect(groupingBy(Coordinate::row, toSet()));

        int enclosedTiles = 0;
        for (Integer row : coordinatesPerRow.keySet()) {
            char[] rowContent = matrix[row];
            int seenPipes = 0;
            Direction pendingPipeInDirection = null;

            for (char columnContent : rowContent) {
                if (columnContent == '|') {
                    seenPipes++;
                } else if (columnContent == '-') {
                    if (pendingPipeInDirection == null) {
                        throw new IllegalStateException();
                    }
                } else if (columnContent == 'L' || columnContent == 'F') {
                    if (pendingPipeInDirection != null) {
                        throw new IllegalStateException();
                    } else {
                        pendingPipeInDirection = columnContent == 'F' ? Direction.SOUTH : Direction.NORTH;
                    }
                } else if (columnContent == 'J' || columnContent == '7') {
                    if (pendingPipeInDirection != null) {
                        if (columnContent == '7' && pendingPipeInDirection == Direction.SOUTH ||
                                columnContent == 'J' && pendingPipeInDirection == Direction.NORTH) {
                            seenPipes += 2;
                        } else {
                            seenPipes += 1;
                        }

                        pendingPipeInDirection = null;
                    } else {
                        throw new IllegalStateException();
                    }
                } else if (columnContent == ' ') {
                    if (seenPipes % 2 == 1) {
                        enclosedTiles++;
                    }
                }
            }

            if (seenPipes % 2 == 1) {
                throw new IllegalStateException("row " + row);
            } else if (pendingPipeInDirection != null) {
                throw new IllegalStateException("row " + row);
            }
        }

        return enclosedTiles;
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
                 Set<Character> west) {

            this.ch = ch;
            this.north = north;
            this.east = east;
            this.south = south;
            this.west = west;
        }
    }

    enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST;
    }
}
