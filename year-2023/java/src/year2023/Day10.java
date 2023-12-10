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

    /**
     * The landscape here is alien; even the flowers and trees are made of metal. As you stop to
     * admire some metal grass, you notice something metallic scurry away in your peripheral vision
     * and jump into a big pipe! It didn't look like any animal you've ever seen; if you want a
     * better look, you'll need to get ahead of it.
     *
     * Scanning the area, you discover that the entire field you're standing on is densely packed
     * with pipes; it was hard to tell at first because they're the same metallic silver color as
     * the "ground". You make a quick sketch of all of the surface pipes you can see (your puzzle
     * input).
     *
     * The pipes are arranged in a two-dimensional grid of tiles:
     *
     *     | is a vertical pipe connecting north and south.
     *     - is a horizontal pipe connecting east and west.
     *     L is a 90-degree bend connecting north and east.
     *     J is a 90-degree bend connecting north and west.
     *     7 is a 90-degree bend connecting south and west.
     *     F is a 90-degree bend connecting south and east.
     *     . is ground; there is no pipe in this tile.
     *     S is the starting position of the animal; there is a pipe on this tile, but your sketch
     *     doesn't show what shape the pipe has.
     *
     * Based on the acoustics of the animal's scurrying, you're confident the pipe that contains
     * the animal is one large, continuous loop.
     *
     * For example, here is a square loop of pipe:
     *
     * .....
     * .F-7.
     * .|.|.
     * .L-J.
     * .....
     *
     * If the animal had entered this loop in the northwest corner, the sketch would instead look
     * like this:
     *
     * .....
     * .S-7.
     * .|.|.
     * .L-J.
     * .....
     *
     * In the above diagram, the S tile is still a 90-degree F bend: you can tell because of how
     * the adjacent pipes connect to it.
     *
     * Unfortunately, there are also many pipes that aren't connected to the loop! This sketch
     * shows the same loop as above:
     *
     * -L|F7
     * 7S-7|
     * L|7||
     * -L-J|
     * L|-JF
     *
     * In the above diagram, you can still figure out which pipes form the main loop: they're the
     * ones connected to S, pipes those pipes connect to, pipes those pipes connect to, and so on.
     * Every pipe in the main loop connects to its two neighbors (including S, which will have
     * exactly two pipes connecting to it, and which is assumed to connect back to those two pipes).
     *
     * Here is a sketch that contains a slightly more complex main loop:
     *
     * ..F7.
     * .FJ|.
     * SJ.L7
     * |F--J
     * LJ...
     *
     * Here's the same example sketch with the extra, non-main-loop pipe tiles also shown:
     *
     * 7-F7-
     * .FJ|7
     * SJLL7
     * |F--J
     * LJ.LJ
     *
     * If you want to get out ahead of the animal, you should find the tile in the loop that is
     * farthest from the starting position. Because the animal is in the pipe, it doesn't make
     * sense to measure this by direct distance. Instead, you need to find the tile that would take
     * 'the longest number of steps along the loop to reach from the starting point - regardless of
     * which way around the loop the animal went.
     *
     * In the first example with the square loop:
     *
     * .....
     * .S-7.
     * .|.|.
     * .L-J.
     * .....
     *
     * You can count the distance each tile in the loop is from the starting point like this:
     *
     * .....
     * .012.
     * .1.3.
     * .234.
     * .....
     *
     * In this example, the farthest point from the start is 4 steps away.
     *
     * Here's the more complex loop again:
     *
     * ..F7.
     * .FJ|.
     * SJ.L7
     * |F--J
     * LJ...
     *
     * Here are the distances for each tile on that loop:
     *
     * ..45.
     * .236.
     * 01.78
     * 14567
     * 23...
     *
     * Find the single giant loop starting at S. How many steps along the loop does it take to get
     * from the starting position to the point farthest from the starting position?
     *
     * Your puzzle answer was 6757.
     */
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

        return steps / 2;
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
