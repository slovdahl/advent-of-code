package year2023;

import lib.Day;
import lib.Matrix;
import org.jetbrains.annotations.NotNull;
import lib.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static lib.Matrix.manhattanDistance;

@SuppressWarnings("unused")
public class Day11 extends Day {

    @Override
    protected Object part1(Stream<String> input) throws IOException {
        int expansionFactor = 2;

        return calculateSumOfShortestPathsBetweenGalaxies(input, expansionFactor); // Your puzzle answer was 9947476
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        int expansionFactor = 1_000_000;

        return calculateSumOfShortestPathsBetweenGalaxies(input, expansionFactor); // Your puzzle answer was 519939907614
    }

    private static long calculateSumOfShortestPathsBetweenGalaxies(Stream<String> input, int expansionFactor) {
        char[][] matrix = Matrix.matrix(input.toList());

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
                        manhattanDistance(
                                pair.first().row, pair.first().column,
                                pair.second().row, pair.second().column
                        )
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
