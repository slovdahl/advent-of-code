package year2023;

import year2023.tools.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UseOfSystemOutOrSystemErr"})
public class Day18 extends Day {

    @Override
    Long part1(Stream<String> input) throws IOException {
        Matrix matrix = new Matrix();
        Coordinate current = new Coordinate(0, 0);

        for (String line : input.toList()) {
            String[] split = line.split(" ");

            Direction direction = from(split[0]);
            int steps = Integer.parseInt(split[1]);

            current = matrix.add(
                    current,
                    direction,
                    steps
            );
        }

        System.out.println("Current: " + current);

        matrix.print();

        int enclosedTiles = 0;
        for (int row = 0; row < matrix.rows(); row++) {
            List<Character> rowContent = matrix.row(row);

            int seenEdges = 0;
            Direction pendingEdgeInDirection = null;

            for (int column = 0; column < rowContent.size(); column++) {
                Character columnContent = rowContent.get(column);

                if (columnContent == '#') {
                    boolean up = false;
                    boolean down = false;
                    if (row > 0 && matrix.row(row - 1).get(column) == '#') {
                        up = true;
                    }
                    if (row < matrix.rows() - 1 && matrix.row(row + 1).get(column) == '#') {
                        down = true;
                    }

                    if (pendingEdgeInDirection != null) {
                        if (up && pendingEdgeInDirection == Direction.DOWN ||
                                down && pendingEdgeInDirection == Direction.UP) {

                            pendingEdgeInDirection = null;
                        } else if (up && pendingEdgeInDirection == Direction.UP ||
                                down && pendingEdgeInDirection == Direction.DOWN) {

                            seenEdges++;
                            pendingEdgeInDirection = null;
                        } else if (!up && !down) {
                            continue;
                        } else {
                            throw new IllegalStateException();
                        }
                    } else {
                        seenEdges++;

                        if (up && down) {
                            continue;
                        } else if (up) {
                            pendingEdgeInDirection = Direction.UP;
                        } else if (down) {
                            pendingEdgeInDirection = Direction.DOWN;
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                } else if (columnContent == '.') {
                    if (seenEdges % 2 == 1) {
                        matrix.row(row).set(column, '!');
                        enclosedTiles++;
                    }
                }
            }

            if (seenEdges % 2 == 1) {
                throw new IllegalStateException("row " + row);
            } else if (pendingEdgeInDirection != null) {
                throw new IllegalStateException("row " + row);
            }
        }

        matrix.print();

        return matrix.stream()
                .parallel()
                .flatMap(Collection::stream)
                .filter(c -> c == '#' || c == '!')
                .count();
    }

    private static Direction from(String d) {
        return switch (d) {
            case "U" -> Direction.UP;
            case "R" -> Direction.RIGHT;
            case "D" -> Direction.DOWN;
            case "L" -> Direction.LEFT;
            default -> throw new IllegalStateException();
        };
    }

    private static class Matrix {
        private final List<List<Character>> matrix;
        private int count;

        Matrix() {
            matrix = new ArrayList<>();
        }

        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        void print() {
            System.out.println();
            for (List<Character> characters : matrix) {
                for (Character ch : characters) {
                    System.out.print(ch);
                }
                System.out.println();
            }
            System.out.println();
        }

        Coordinate add(Coordinate startingPoint, Direction direction, int steps) {
            if (matrix.isEmpty()) {
                matrix.add(newRow());
            }

            if (direction == Direction.UP) {
                if (startingPoint.row - steps < 0) {
                    int rowsToAdd = Math.abs(startingPoint.row - steps);
                    for (int i = 0; i < rowsToAdd; i++) {
                        matrix.addFirst(newRow());
                    }
                    startingPoint = new Coordinate(startingPoint.row + rowsToAdd, startingPoint.column);
                }

                for (int step = 1; step <= steps; step++) {
                    List<Character> row = matrix.get(startingPoint.row - step);
                    row.set(startingPoint.column, '#');
                }

                return new Coordinate(startingPoint.row - steps, startingPoint.column);
            } else if (direction == Direction.RIGHT) {
                List<Character> row = matrix.get(startingPoint.row);
                int end = startingPoint.column + steps;
                increaseRowLengthsTo(end + 1);

                for (int column = startingPoint.column + 1; column <= end; column++) {
                    row.set(column, '#');
                }

                return new Coordinate(startingPoint.row, startingPoint.column + steps);
            } else if (direction == Direction.DOWN) {
                if (startingPoint.row + steps >= matrix.size()) {
                    int rowsToAdd = startingPoint.row + steps - matrix.size();
                    for (int i = 0; i <= rowsToAdd; i++) {
                        matrix.add(newRow());
                    }
                }

                for (int step = 1; step <= steps; step++) {
                    List<Character> row = matrix.get(startingPoint.row + step);
                    row.set(startingPoint.column, '#');
                }

                return new Coordinate(startingPoint.row + steps, startingPoint.column);
            } else if (direction == Direction.LEFT) {
                List<Character> row = matrix.get(startingPoint.row);

                int end = startingPoint.column - steps;

                if (end < 0) {
                    prependColumns(Math.abs(startingPoint.column - steps));
                    startingPoint = new Coordinate(startingPoint.row, startingPoint.column + steps);
                    end = 0;
                }

                for (int column = startingPoint.column; column >= end; column--) {
                    row.set(column, '#');
                }

                return new Coordinate(startingPoint.row, startingPoint.column - steps);
            }

            throw new IllegalStateException();
        }

        List<Character> row(int row) {
            while (matrix.size() - 1 < row) {
                matrix.add(newRow());
            }

            return matrix.get(row);
        }

        int rows() {
            return matrix.size();
        }

        int columns() {
            return !matrix.isEmpty() ? matrix.getFirst().size() : 0;
        }

        Stream<List<Character>> stream() {
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

    private record Coordinate(int row, int column) {
    }
}
