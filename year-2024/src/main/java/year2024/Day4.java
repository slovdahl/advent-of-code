package year2024;

import lib.Day;
import lib.Matrix;

import java.util.stream.Stream;

import static lib.Matrix.findDiagonalDownLeft;
import static lib.Matrix.findDiagonalDownRight;
import static lib.Matrix.findDiagonalUpLeft;
import static lib.Matrix.findDiagonalUpRight;
import static lib.Matrix.findDown;
import static lib.Matrix.findLeft;
import static lib.Matrix.findRight;
import static lib.Matrix.findUp;

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

                if (findRight("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findLeft("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findUp("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findDown("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findDiagonalDownLeft("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findDiagonalDownRight("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findDiagonalUpLeft("XMAS", matrix, i, j)) {
                    sum++;
                }
                if (findDiagonalUpRight("XMAS", matrix, i, j)) {
                    sum++;
                }
            }
        }

        return sum; // Your puzzle answer was 2571
    }

    @Override
    protected Object part2(Stream<String> input) {
        int sum = 0;

        char[][] matrix = Matrix.matrix(input.toList());

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 'A' ||
                        i == 0 || j == 0 ||
                        i == matrix.length - 1 || j == matrix[i].length - 1) {

                    continue;
                }

                if ((findDiagonalDownLeft("MAS", matrix, i - 1, j + 1) || findDiagonalDownLeft("SAM", matrix, i - 1, j + 1)) &&
                        (findDiagonalDownRight("MAS", matrix, i - 1, j - 1) || findDiagonalDownRight("SAM", matrix, i - 1, j - 1))) {

                    sum++;
                }
            }
        }

        return sum; // Your puzzle answer was 1992
    }
}
