package lib;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public record Coordinate(int row, int column) {

    public Coordinate(String rows, String columns) {
        this(Integer.parseInt(rows), Integer.parseInt(columns));
    }

    public char at(char[][] matrix) {
        if (row >= 0 && row < matrix.length &&
                column >= 0 && column < matrix[0].length) {

            return matrix[row][column];
        }

        throw new NoSuchElementException("Coordinate " + this + " out of bounds in matrix");
    }

    public int at(int[][] matrix) {
        if (row >= 0 && row < matrix.length &&
                column >= 0 && column < matrix[0].length) {

            return matrix[row][column];
        }

        throw new NoSuchElementException("Coordinate " + this + " out of bounds in matrix");
    }

    public char at(char[][] matrix, char fallback) {
        if (row >= 0 && row < matrix.length &&
                column >= 0 && column < matrix[0].length) {

            return matrix[row][column];
        }

        return fallback;
    }

    public char atDirection(char[][] matrix, Direction direction, char fallback) {
        return switch (direction) {
            case UP -> upOr(matrix, fallback);
            case RIGHT -> rightOr(matrix, fallback);
            case DOWN -> downOr(matrix, fallback);
            case LEFT -> leftOr(matrix, fallback);
        };
    }

    public int atDirection(int[][] matrix, Direction direction, int fallback) {
        return switch (direction) {
            case UP -> upOr(matrix, fallback);
            case RIGHT -> rightOr(matrix, fallback);
            case DOWN -> downOr(matrix, fallback);
            case LEFT -> leftOr(matrix, fallback);
        };
    }

    @CanIgnoreReturnValue
    public char set(char[][] matrix, char ch) {
        char old = matrix[row][column];
        matrix[row][column] = ch;
        return old;
    }

    public char north(char[][] matrix) {
        return matrix[row - 1][column];
    }

    public char upOr(char[][] matrix, char fallback) {
        if (row >= 1) {
            return matrix[row - 1][column];
        } else {
            return fallback;
        }
    }

    public int upOr(int[][] matrix, int fallback) {
        if (row >= 1) {
            return matrix[row - 1][column];
        } else {
            return fallback;
        }
    }

    public char east(char[][] matrix) {
        return matrix[row][column + 1];
    }

    public char rightOr(char[][] matrix, char fallback) {
        if (column + 1 < matrix[row].length) {
            return matrix[row][column + 1];
        } else {
            return fallback;
        }
    }

    public int rightOr(int[][] matrix, int fallback) {
        if (column + 1 < matrix[row].length) {
            return matrix[row][column + 1];
        } else {
            return fallback;
        }
    }

    public char south(char[][] matrix) {
        return matrix[row + 1][column];
    }

    public char downOr(char[][] matrix, char fallback) {
        if (row + 1 < matrix.length) {
            return matrix[row + 1][column];
        } else {
            return fallback;
        }
    }

    public int downOr(int[][] matrix, int fallback) {
        if (row + 1 < matrix.length) {
            return matrix[row + 1][column];
        } else {
            return fallback;
        }
    }

    public char west(char[][] matrix) {
        return matrix[row][column - 1];
    }

    public char leftOr(char[][] matrix, char fallback) {
        if (column >= 1) {
            return matrix[row][column - 1];
        } else {
            return fallback;
        }
    }

    public int leftOr(int[][] matrix, int fallback) {
        if (column >= 1) {
            return matrix[row][column - 1];
        } else {
            return fallback;
        }
    }

    public Coordinate toNorth() {
        return moveUp();
    }

    public Coordinate moveUp() {
        return new Coordinate(row - 1, column);
    }

    public Coordinate toEast() {
        return moveRight();
    }

    public Coordinate moveRight() {
        return new Coordinate(row, column + 1);
    }

    public Coordinate toSouth() {
        return moveDown();
    }

    public Coordinate moveDown() {
        return new Coordinate(row + 1, column);
    }

    public Coordinate toWest() {
        return moveLeft();
    }

    public Coordinate moveLeft() {
        return new Coordinate(row, column - 1);
    }

    public Coordinate withRow(int row) {
        return new Coordinate(row, column);
    }

    public Coordinate withColumn(int column) {
        return new Coordinate(row, column);
    }

    public Coordinate move(Direction direction) {
        return switch (direction) {
            case UP -> moveUp();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
            case RIGHT -> moveRight();
        };
    }

    public Coordinate moveWithWraparound(Object[][] matrix, long times, int deltaRow, int deltaColumn) {
        if (times == 0) {
            return this;
        }

        int newRow = (int) ((row + (times * deltaRow)) % (long) matrix.length);
        if (newRow < 0) {
            newRow = matrix.length - Math.abs(newRow);
        }

        int newColumn = (int) ((column + (times * deltaColumn)) % (long) matrix[row].length);
        if (newColumn < 0) {
            newColumn = matrix[row].length - Math.abs(newColumn);
        }

        return new Coordinate(newRow, newColumn);
    }

    @Nullable
    public Coordinate tryMove(char[][] matrix, Direction direction) {
        return switch (direction) {
            case UP -> {
                if (row > 0) {
                    yield moveUp();
                } else {
                    yield null;
                }
            }
            case DOWN -> {
                if (row < matrix.length - 1) {
                    yield moveDown();
                } else {
                    yield null;
                }
            }
            case LEFT -> {
                if (column > 0) {
                    yield moveLeft();
                } else {
                    yield null;
                }
            }
            case RIGHT -> {
                if (column < matrix[row].length - 1) {
                    yield moveRight();
                } else {
                    yield null;
                }
            }
        };
    }

    @Nullable
    public Coordinate tryMove(int[][] matrix, Direction direction) {
        return switch (direction) {
            case UP -> {
                if (row > 0) {
                    yield moveUp();
                } else {
                    yield null;
                }
            }
            case DOWN -> {
                if (row < matrix.length - 1) {
                    yield moveDown();
                } else {
                    yield null;
                }
            }
            case LEFT -> {
                if (column > 0) {
                    yield moveLeft();
                } else {
                    yield null;
                }
            }
            case RIGHT -> {
                if (column < matrix[row].length - 1) {
                    yield moveRight();
                } else {
                    yield null;
                }
            }
        };
    }

    @Nullable
    public Coordinate tryMove(char[][] matrix, int rowDelta, int columnDelta) {
        Coordinate newCoordinate = new Coordinate(row + rowDelta, column + columnDelta);
        if (newCoordinate.in(matrix)) {
            return newCoordinate;
        } else {
            return null;
        }
    }

    public Direction directionTo(Coordinate other) {
        if (row == other.row && column == other.column) {
            throw new IllegalStateException("no direction, same coordinate");
        }

        if (row == other.row) {
            if (other.column > column) {
                return Direction.RIGHT;
            } else {
                return Direction.LEFT;
            }
        } else if (column == other.column) {
            if (other.row > row) {
                return Direction.DOWN;
            } else {
                return Direction.UP;
            }
        } else {
            throw new IllegalStateException("diagonal directions unsupported (from " + this + " to " + other + ")");
        }
    }

    public boolean in(char[][] map) {
        return row >= 0 && row < map.length &&
                column >= 0 && column < map[row].length;
    }

    public boolean in(int[][] map) {
        return row >= 0 && row < map.length &&
                column >= 0 && column < map[row].length;
    }

    public static Direction from(String d) {
        return switch (d) {
            case "U", "^" -> Direction.UP;
            case "R", ">" -> Direction.RIGHT;
            case "D", "v" -> Direction.DOWN;
            case "L", "<" -> Direction.LEFT;
            default -> throw new IllegalStateException();
        };
    }

    public static List<Direction> toDirections(List<Coordinate> coordinates) {
        List<Direction> directions = new ArrayList<>(coordinates.size() - 1);

        List<Coordinate> c = new ArrayList<>(coordinates);
        Coordinate previous = c.removeFirst();
        for (Coordinate coordinate : c) {
            directions.add(previous.directionTo(coordinate));
            previous = coordinate;
        }

        return directions;
    }
}
