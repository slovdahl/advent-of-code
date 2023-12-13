package year2023;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
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
        int expansionFactor = 2;

        return calculateSumOfShortestPathsBetweenGalaxies(input, expansionFactor);
    }

    /**
     * The galaxies are much older (and thus much farther apart) than the researcher initially
     * estimated.
     *
     * Now, instead of the expansion you did before, make each empty row or column one million
     * times larger. That is, each empty row should be replaced with 1000000 empty rows, and each
     * empty column should be replaced with 1000000 empty columns.
     *
     * (In the example above, if each empty row or column were merely 10 times larger, the sum of
     * the shortest paths between every pair of galaxies would be 1030. If each empty row or column
     * were merely 100 times larger, the sum of the shortest paths between every pair of galaxies
     * would be 8410. However, your universe will need to expand far beyond these values.)
     *
     * Starting with the same initial image, expand the universe according to these new rules, then
     * find the length of the shortest path between every pair of galaxies. What is the sum of
     * these lengths?
     *
     * Your puzzle answer was 519939907614.
     */
    @Override
    Object part2(Stream<String> input) throws Exception {
        int expansionFactor = 1_000_000;

        return calculateSumOfShortestPathsBetweenGalaxies(input, expansionFactor);
    }

    private static long calculateSumOfShortestPathsBetweenGalaxies(Stream<String> input, int expansionFactor) {
        char[][] matrix = Common.matrix(input.toList());

        Set<Integer> rowsToExpand = getRowsToExpand(matrix);
        Set<Integer> columnsToExpand = getColumnsToExpand(matrix);

        List<Galaxy> galaxies = new ArrayList<>();
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[0].length; column++) {
                if (matrix[row][column] == '#') {
                    galaxies.add(new Galaxy(row, column));
                }
            }
        }

        List<Pair<Galaxy, Galaxy>> pairs = new ArrayList<>();
        for (int element1 = 0; element1 < galaxies.size(); element1++) {
            for (int element2 = element1 + 1; element2 < galaxies.size(); element2++) {
                Galaxy galaxy1 = galaxies.get(element1);
                Galaxy galaxy2 = galaxies.get(element2);

                if (galaxy2.row + 1 > galaxy1.row) {
                    galaxy2 = galaxy2.withRow(
                            expandSpaceBetweenGalaxies(galaxy1.row, galaxy2.row, rowsToExpand, expansionFactor)
                    );
                } else if (galaxy1.row + 1 > galaxy2.row) {
                    galaxy1 = galaxy1.withRow(
                            expandSpaceBetweenGalaxies(galaxy2.row, galaxy1.row, rowsToExpand, expansionFactor)
                    );
                }

                if (galaxy2.column + 1 > galaxy1.column) {
                    galaxy2 = galaxy2.withColumn(
                            expandSpaceBetweenGalaxies(galaxy1.column, galaxy2.column, columnsToExpand, expansionFactor)
                    );
                } else if (galaxy1.column + 1 > galaxy2.column) {
                    galaxy1 = galaxy1.withColumn(
                            expandSpaceBetweenGalaxies(galaxy2.column, galaxy1.column, columnsToExpand, expansionFactor)
                    );
                }

                pairs.add(new Pair<>(galaxy1, galaxy2));
            }
        }

        return pairs.stream()
                .mapToLong(pair ->
                        Math.abs(pair.first().row - pair.second().row) +
                                Math.abs(pair.first().column - pair.second().column)
                )
                .sum();
    }

    private static int expandSpaceBetweenGalaxies(int row1, int row2, Set<Integer> toExpand, int expansionFactor) {
        int count = (int) IntStream.range(row1 + 1, row2)
                .filter(toExpand::contains)
                .count();

        return row2 + count * (expansionFactor - 1);
    }

    private static Set<Integer> getRowsToExpand(char[][] matrix) {
        Set<Integer> rowNumbers = new HashSet<>();

        for (int i = 0; i < matrix.length; i++) {
            char[] row = matrix[i];
            boolean galaxySeen = false;
            for (char spaceOrGalaxy : row) {
                if (spaceOrGalaxy == '#') {
                    galaxySeen = true;
                    break;
                }
            }

            if (!galaxySeen) {
                rowNumbers.add(i);
            }
        }

        return rowNumbers;
    }

    private static Set<Integer> getColumnsToExpand(char[][] matrix) {
        Set<Integer> columnNumbers = new HashSet<>();

        for (int column = 0; column < matrix[0].length; column++) {
            boolean galaxySeen = false;
            for (char[] row : matrix) {
                if (row[column] == '#') {
                    galaxySeen = true;
                    break;
                }
            }

            if (!galaxySeen) {
                columnNumbers.add(column);
            }
        }

        return columnNumbers;
    }

    record Galaxy(int row, int column) implements Comparable<Galaxy> {
        public Galaxy withRow(int newRow) {
            return new Galaxy(newRow, column);
        }

        public Galaxy withColumn(int newColumn) {
            return new Galaxy(row, newColumn);
        }

        @Override
        public int compareTo(@NotNull Galaxy other) {
            return Comparator
                    .comparing(Galaxy::row)
                    .thenComparing(Galaxy::column)
                    .compare(this, other);
        }
    }
}
