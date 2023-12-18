package year2023;

import java.io.IOException;
import java.util.stream.Stream;

import static year2023.Common.matrix;
import static year2023.Common.swap;

@SuppressWarnings("unused")
public class Day14 extends Day {

    @Override
    Integer part1(Stream<String> input) throws IOException {
        char[][] matrix = matrix(input.toList());

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

        int load = 0;
        for (int row = 0; row < matrix.length; row++) {
            for (int column = 0; column < matrix[row].length; column++) {
                if (matrix[row][column] == 'O') {
                    load += (matrix.length - row);
                }
            }
        }

        return load; // Your puzzle answer was 110407
    }
}
