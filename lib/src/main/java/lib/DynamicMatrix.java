package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DynamicMatrix {

    private final List<List<Character>> matrix;
    private int count;

    DynamicMatrix() {
        matrix = new ArrayList<>();
    }

    public void print() {
        System.out.println();
        for (List<Character> characters : matrix) {
            for (Character ch : characters) {
                System.out.print(ch);
            }
            System.out.println();
        }
        System.out.println();
    }

    public Coordinate add(Coordinate startingPoint, Direction direction, int steps) {
        if (matrix.isEmpty()) {
            matrix.add(newRow());
        }

        if (direction == Direction.UP) {
            if (startingPoint.row() - steps < 0) {
                int rowsToAdd = Math.abs(startingPoint.row() - steps);
                for (int i = 0; i < rowsToAdd; i++) {
                    matrix.addFirst(newRow());
                }
                startingPoint = new Coordinate(startingPoint.row() + rowsToAdd, startingPoint.column());
            }

            for (int step = 1; step <= steps; step++) {
                List<Character> row = matrix.get(startingPoint.row() - step);
                row.set(startingPoint.column(), '#');
            }

            return new Coordinate(startingPoint.row() - steps, startingPoint.column());
        } else if (direction == Direction.RIGHT) {
            List<Character> row = matrix.get(startingPoint.row());
            int end = startingPoint.column() + steps;
            increaseRowLengthsTo(end + 1);

            for (int column = startingPoint.column() + 1; column <= end; column++) {
                row.set(column, '#');
            }

            return new Coordinate(startingPoint.row(), startingPoint.column() + steps);
        } else if (direction == Direction.DOWN) {
            if (startingPoint.row() + steps >= matrix.size()) {
                int rowsToAdd = startingPoint.row() + steps - matrix.size();
                for (int i = 0; i <= rowsToAdd; i++) {
                    matrix.add(newRow());
                }
            }

            for (int step = 1; step <= steps; step++) {
                List<Character> row = matrix.get(startingPoint.row() + step);
                row.set(startingPoint.column(), '#');
            }

            return new Coordinate(startingPoint.row() + steps, startingPoint.column());
        } else if (direction == Direction.LEFT) {
            List<Character> row = matrix.get(startingPoint.row());

            int end = startingPoint.column() - steps;

            if (end < 0) {
                prependColumns(Math.abs(startingPoint.column() - steps));
                startingPoint = new Coordinate(startingPoint.row(), startingPoint.column() + steps);
                end = 0;
            }

            for (int column = startingPoint.column(); column >= end; column--) {
                row.set(column, '#');
            }

            return new Coordinate(startingPoint.row(), startingPoint.column() - steps);
        }

        throw new IllegalStateException();
    }

    List<Character> row(int row) {
        while (matrix.size() - 1 < row) {
            matrix.add(newRow());
        }

        return matrix.get(row);
    }

    public int rows() {
        return matrix.size();
    }

    public int columns() {
        return !matrix.isEmpty() ? matrix.getFirst().size() : 0;
    }

    public Stream<List<Character>> stream() {
        return matrix.stream();
    }

    private Character get(int row, int column) {
        while (matrix.size() - 1 < row) {
            matrix.add(newRow());
        }

        if (matrix.getFirst().size() - 1 < column) {
            increaseRowLengthsTo(column + 1);
        }

        return matrix.get(row).get(column);
    }

    private List<Character> newRow() {
        List<Character> row = new ArrayList<>();

        if (!matrix.isEmpty()) {
            for (int i = 0; i < matrix.getFirst().size(); i++) {
                row.add('.');
            }
        }

        return row;
    }

    private void increaseRowLengthsTo(int length) {
        for (List<Character> row : matrix) {
            if (row.size() < length) {
                int numberToAdd = length - row.size();
                for (int column = 0; column < numberToAdd; column++) {
                    row.add('.');
                }
            }
        }
    }

    private void prependColumns(int columns) {
        for (List<Character> row : matrix) {
            for (int i = 0; i < columns; i++) {
                row.addFirst('.');
            }
        }
    }
}
