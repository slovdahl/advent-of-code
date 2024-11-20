package year2023;

import lib.Day;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static lib.Matrix.matrix;
import static lib.Common.swap;

@SuppressWarnings("unused")
public class Day14 extends Day {

    @Override
    protected Integer part1(Stream<String> input) throws IOException {
        char[][] matrix = matrix(input.toList());

        tiltAllRocksNorth(matrix);

        return calculateLoad(matrix); // Your puzzle answer was 110407
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        char[][] matrix = matrix(input.toList());

        Set<char[][]> seen = new HashSet<>();

        int period = 13;
        int[] valuesInPeriod = new int[period];

        int previous = calculateLoad(matrix);

        for (int n = 1; n <= 1_000; n++) {
            tiltAllRocksNorth(matrix);
            tiltAllRocksWest(matrix);
            tiltAllRocksSouth(matrix);
            tiltAllRocksEast(matrix);

            int current = calculateLoad(matrix);
            System.out.printf("Load diff: %7d %7d%n", (previous - current), current);
            previous = current;

            valuesInPeriod[n % period] = current;
        }

        return valuesInPeriod[1_000_000_000 % period]; // Your puzzle answer was 87273
    }

    private static void tiltAllRocksNorth(char[][] matrix) {
        for (int column = 0; column < matrix[0].length; column++) {
            int nextFreeRow = matrix[0][column] == '.' ? 0 : -1;
            for (int row = 1; row < matrix.length; row++) {
                char current = matrix[row][column];
                if (current == 'O') {
                    if (nextFreeRow >= 0) {
                        swap(matrix[row], matrix[nextFreeRow], column, column);
                        nextFreeRow = nextFreeRow + 1;
                    } else {
                        nextFreeRow = -1;
                    }
                } else if (current == '.') {
                    if (nextFreeRow >= 0 && nextFreeRow < row) {
                        continue;
                    } else {
                        nextFreeRow = row;
                    }
                } else if (current == '#') {
                    nextFreeRow = -1;
                }
            }
        }
    }

    private static void tiltAllRocksWest(char[][] matrix) {
        for (char[] row : matrix) {
            int nextFreeColumn = row[0] == '.' ? 0 : -1;

            for (int column = 0; column < row.length; column++) {
                char current = row[column];
                if (current == 'O') {
                    if (nextFreeColumn >= 0) {
                        swap(row, nextFreeColumn, column);
                        nextFreeColumn++;
                    } else {
                        nextFreeColumn = -1;
                    }
                } else if (current == '.') {
                    if (nextFreeColumn >= 0 && nextFreeColumn < column) {
                        continue;
                    } else {
                        nextFreeColumn = column;
                    }
                } else if (current == '#') {
                    nextFreeColumn = -1;
                }
            }
        }
    }

    private static void tiltAllRocksSouth(char[][] matrix) {
        for (int column = 0; column < matrix[0].length; column++) {
            int nextFreeRow = matrix[matrix.length - 1][column] == '.' ? 0 : -1;
            for (int row = matrix.length - 1; row >= 0; row--) {
                char current = matrix[row][column];
                if (current == 'O') {
                    if (nextFreeRow > 0) {
                        swap(matrix[row], matrix[nextFreeRow], column, column);
                        nextFreeRow--;
                    } else {
                        nextFreeRow = -1;
                    }
                } else if (current == '.') {
                    if (nextFreeRow > row) {
                        continue;
                    } else {
                        nextFreeRow = row;
                    }
                } else if (current == '#') {
                    nextFreeRow = -1;
                }
            }
        }
    }

    private static void tiltAllRocksEast(char[][] matrix) {
        for (char[] row : matrix) {
            int nextFreeColumn = row[row.length - 1] == '.' ? 0 : -1;

            for (int column = row.length - 1; column >= 0; column--) {
                char current = row[column];
                if (current == 'O') {
                    if (nextFreeColumn >= 0) {
                        swap(row, nextFreeColumn, column);
                        nextFreeColumn--;
                    } else {
                        nextFreeColumn = -1;
                    }
                } else if (current == '.') {
                    if (nextFreeColumn > column) {
                        continue;
                    } else {
                        nextFreeColumn = column;
                    }
                } else if (current == '#') {
                    nextFreeColumn = -1;
                }
            }
        }
    }


    private static int calculateLoad(char[][] matrix) {
        int load = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == 'O') {
                    load += matrix.length - row;
                }
            }
        }
        return load;
    }
}
