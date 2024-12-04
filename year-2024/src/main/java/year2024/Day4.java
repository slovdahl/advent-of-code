package year2024;

import lib.Day;
import lib.Matrix;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day4 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        int sum = 0;

        char[][] matrix = Matrix.matrix(input.toList());

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                char ch = matrix[i][j];
                if (ch != 'X') {
                    continue;
                }

                if (findXmasRight(matrix, i, j)) {
                    sum++;
                }
                if (findXmasLeft(matrix, i, j)) {
                    sum++;
                }
                if (findXmasUp(matrix, i, j)) {
                    sum++;
                }
                if (findXmasDown(matrix, i, j)) {
                    sum++;
                }
                if (findXmasDiagonalDownLeft(matrix, i, j)) {
                    sum++;
                }
                if (findXmasDiagonalDownRight(matrix, i, j)) {
                    sum++;
                }
                if (findXmasDiagonalUpLeft(matrix, i, j)) {
                    sum++;
                }
                if (findXmasDiagonalUpRight(matrix, i, j)) {
                    sum++;
                }
            }
        }

        return sum; // Your puzzle answer was 2571
    }

    private static boolean findXmasRight(char[][] matrix, int i, int j) {
        if (matrix[i].length <= j + 3) {
            return false;
        }

        return matrix[i][j + 1] == 'M' && matrix[i][j + 2] == 'A' && matrix[i][j + 3] == 'S';
    }

    private static boolean findXmasLeft(char[][] matrix, int i, int j) {
        if (j < 3) {
            return false;
        }

        return matrix[i][j - 1] == 'M' && matrix[i][j - 2] == 'A' && matrix[i][j - 3] == 'S';
    }

    private static boolean findXmasUp(char[][] matrix, int i, int j) {
        if (i < 3) {
            return false;
        }

        return matrix[i - 1][j] == 'M' && matrix[i - 2][j] == 'A' && matrix[i - 3][j] == 'S';
    }

    private static boolean findXmasDown(char[][] matrix, int i, int j) {
        if (matrix.length <= i + 3) {
            return false;
        }

        return matrix[i + 1][j] == 'M' && matrix[i + 2][j] == 'A' && matrix[i + 3][j] == 'S';
    }

    private static boolean findXmasDiagonalUpLeft(char[][] matrix, int i, int j) {
        if (i < 3 || j < 3) {
            return false;
        }

        return matrix[i - 1][j - 1] == 'M' && matrix[i - 2][j - 2] == 'A' && matrix[i - 3][j - 3] == 'S';
    }

    private static boolean findXmasDiagonalUpRight(char[][] matrix, int i, int j) {
        if (i < 3 || matrix[i].length <= j + 3) {
            return false;
        }

        return matrix[i - 1][j + 1] == 'M' && matrix[i - 2][j + 2] == 'A' && matrix[i - 3][j + 3] == 'S';
    }

    private static boolean findXmasDiagonalDownLeft(char[][] matrix, int i, int j) {
        if (matrix.length <= i + 3 || j < 3) {
            return false;
        }

        return matrix[i + 1][j - 1] == 'M' && matrix[i + 2][j - 2] == 'A' && matrix[i + 3][j - 3] == 'S';
    }

    private static boolean findXmasDiagonalDownRight(char[][] matrix, int i, int j) {
        if (matrix.length <= i + 3 || matrix[i].length <= j + 3) {
            return false;
        }

        return matrix[i + 1][j + 1] == 'M' && matrix[i + 2][j + 2] == 'A' && matrix[i + 3][j + 3] == 'S';
    }
}
