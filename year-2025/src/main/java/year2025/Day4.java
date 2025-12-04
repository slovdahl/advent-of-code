package year2025;

import lib.AnyDirection;
import lib.Coordinate;
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
        char[][] matrix = Matrix.matrix(input.toList());
        char[][] markedMatrix = Matrix.deepClone(matrix);

        int forkliftCanAccess = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != '@') {
                    continue;
                }

                Coordinate position = new Coordinate(i, j);
                int adjacentRolls = 0;

                for (AnyDirection direction : AnyDirection.ALL) {
                    if (adjacentRolls >= 4) {
                        break;
                    }
                    if (position.atDirection(matrix, direction, '.') == '@') {
                        adjacentRolls++;
                    }
                }

                if (adjacentRolls < 4) {
                    markedMatrix[i][j] = 'x';
                    forkliftCanAccess++;
                }
            }
        }

        return forkliftCanAccess; // Your puzzle answer was 1393.
    }
}
