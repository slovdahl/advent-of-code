package year2023.tools;

public record Coordinate(int row, int column) {

    public char north(char[][] matrix) {
        return matrix[row - 1][column];
    }

    public char east(char[][] matrix) {
        return matrix[row][column + 1];
    }

    public char south(char[][] matrix) {
        return matrix[row + 1][column];
    }

    public char west(char[][] matrix) {
        return matrix[row][column - 1];
    }

    public Coordinate toNorth() {
        return new Coordinate(row - 1, column);
    }

    public Coordinate toEast() {
        return new Coordinate(row, column + 1);
    }

    public Coordinate toSouth() {
        return new Coordinate(row + 1, column);
    }

    public Coordinate toWest() {
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
