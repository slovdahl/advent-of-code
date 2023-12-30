package year2023.tools;

public record Coordinate(int row, int column) {

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

    public Coordinate toNorth() {
        return new Coordinate(row - 1, column);
    }

    public Coordinate moveUp() {
        return new Coordinate(row - 1, column);
    }

    public Coordinate toEast() {
        return new Coordinate(row, column + 1);
    }

    public Coordinate moveRight() {
        return new Coordinate(row, column + 1);
    }

    public Coordinate toSouth() {
        return new Coordinate(row + 1, column);
    }

    public Coordinate moveDown() {
        return new Coordinate(row + 1, column);
    }

    public Coordinate toWest() {
        return new Coordinate(row, column - 1);
    }

    public Coordinate moveLeft() {
        return new Coordinate(row, column - 1);
    }

    public static Direction from(String d) {
        return switch (d) {
            case "U" -> Direction.UP;
            case "R" -> Direction.RIGHT;
            case "D" -> Direction.DOWN;
            case "L" -> Direction.LEFT;
            default -> throw new IllegalStateException();
        };
    }
}
