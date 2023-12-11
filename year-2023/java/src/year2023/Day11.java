package year2023;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day11 extends Day {

    /**
     * The researcher has collected a bunch of data and compiled the data into a single giant image
     * (your puzzle input). The image includes empty space (.) and galaxies (#). For example:
     *
     * ...#......
     * .......#..
     * #.........
     * ..........
     * ......#...
     * .#........
     * .........#
     * ..........
     * .......#..
     * #...#.....
     *
     * The researcher is trying to figure out the sum of the lengths of the shortest path between
     * every pair of galaxies. However, there's a catch: the universe expanded in the time it took
     * the light from those galaxies to reach the observatory.
     *
     * Due to something involving gravitational effects, only some space expands. In fact, the
     * result is that any rows or columns that contain no galaxies should all actually be twice as
     * big.
     *
     * In the above example, three columns and two rows contain no galaxies:
     *
     *    v  v  v
     *  ...#......
     *  .......#..
     *  #.........
     * >..........<
     *  ......#...
     *  .#........
     *  .........#
     * >..........<
     *  .......#..
     *  #...#.....
     *    ^  ^  ^
     *
     * These rows and columns need to be twice as big; the result of cosmic expansion therefore
     * looks like this:
     *
     * ....#........
     * .........#...
     * #............
     * .............
     * .............
     * ........#....
     * .#...........
     * ............#
     * .............
     * .............
     * .........#...
     * #....#.......
     *
     * Equipped with this expanded universe, the shortest path between every pair of galaxies can
     * be found. It can help to assign every galaxy a unique number:
     *
     * ....1........
     * .........2...
     * 3............
     * .............
     * .............
     * ........4....
     * .5...........
     * ............6
     * .............
     * .............
     * .........7...
     * 8....9.......
     *
     * In these 9 galaxies, there are 36 pairs. Only count each pair once; order within the pair
     * doesn't matter. For each pair, find any shortest path between the two galaxies using only
     * steps that move up, down, left, or right exactly one . or # at a time. (The shortest path
     * between two galaxies is allowed to pass through another galaxy.)
     *
     * For example, here is one of the shortest paths between galaxies 5 and 9:
     *
     * ....1........
     * .........2...
     * 3............
     * .............
     * .............
     * ........4....
     * .5...........
     * .##.........6
     * ..##.........
     * ...##........
     * ....##...7...
     * 8....9.......
     *
     * This path has length 9 because it takes a minimum of nine steps to get from galaxy 5 to
     * galaxy 9 (the eight locations marked # plus the step onto galaxy 9 itself). Here are some
     * other example shortest path lengths:
     *
     *     Between galaxy 1 and galaxy 7: 15
     *     Between galaxy 3 and galaxy 6: 17
     *     Between galaxy 8 and galaxy 9: 5
     *
     * In this example, after expanding the universe, the sum of the shortest path between all 36
     * pairs of galaxies is 374.
     *
     * Expand the universe, then find the length of the shortest path between every pair of
     * galaxies. What is the sum of these lengths?
     *
     * Your puzzle answer was 9947476.
     */
    @Override
    Object part1(Stream<String> input) throws IOException {
        char[][] matrix = Common.matrix(input.toList());

        matrix = expandRows(matrix);
        matrix = expandColumns(matrix);

        List<Galaxy> galaxies = new ArrayList<>();
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[0].length; column++) {
                if (matrix[row][column] == '#') {
                    galaxies.add(new Galaxy(row, column));
                }
            }
        }

        List<Pair> pairs = new ArrayList<>();
        for (int element1 = 0; element1 < galaxies.size(); element1++) {
            for (int element2 = element1 + 1; element2 < galaxies.size(); element2++) {
                pairs.add(new Pair(galaxies.get(element1), galaxies.get(element2)));
            }
        }

        return pairs.stream()
                .mapToInt(pair -> Math.abs(pair.e1.row - pair.e2.row) + Math.abs(pair.e1.column - pair.e2.column))
                .sum();
    }


    private static char[][] expandRows(char[][] matrix) {
        for (int row = matrix.length - 1; row >= 0; row--) {
            boolean galaxySeen = false;
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == '#') {
                    galaxySeen = true;
                    break;
                }
            }

            if (!galaxySeen) {
                char[][] newMatrix = new char[matrix.length + 1][matrix[0].length];

                System.arraycopy(
                        matrix,
                        0,
                        newMatrix,
                        0,
                        row + 1
                );

                Arrays.fill(newMatrix[row + 1], '.');

                if (row + 1 < matrix.length) {
                    System.arraycopy(
                            matrix,
                            row + 1,
                            newMatrix,
                            row + 2,
                            matrix.length - row - 1
                    );
                }

                matrix = newMatrix;
            }
        }
        return matrix;
    }

    private static char[][] expandColumns(char[][] matrix) {
        for (int column = matrix[0].length - 1; column >= 0; column--) {
            boolean galaxySeen = false;
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][column] == '#') {
                    galaxySeen = true;
                    break;
                }
            }

            if (!galaxySeen) {
                char[][] newMatrix = new char[matrix.length][matrix[0].length + 1];

                for (int row = 0; row < matrix.length; row++) {
                    System.arraycopy(
                            matrix[row],
                            0,
                            newMatrix[row],
                            0,
                            column + 1
                    );

                    newMatrix[row][column + 1] = '.';

                    if (column + 1 < matrix[0].length) {
                        System.arraycopy(
                                matrix[row],
                                column + 1,
                                newMatrix[row],
                                column + 2,
                                matrix[0].length - column - 1
                        );
                    }
                }

                matrix = newMatrix;
            }
        }
        return matrix;
    }

    record Galaxy(int row, int column) implements Comparable<Galaxy> {
        @Override
        public int compareTo(@NotNull Galaxy other) {
            return Comparator
                    .comparing(Galaxy::row)
                    .thenComparing(Galaxy::column)
                    .compare(this, other);
        }
    }

    record Pair(Galaxy e1, Galaxy e2) {
    }
}
