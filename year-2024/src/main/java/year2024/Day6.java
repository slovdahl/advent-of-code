package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day6 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        char[][] map = Matrix.matrix(input.toList());

        Coordinate startingPoint = Matrix.findChar(map, '^');
        Direction direction = Direction.UP;

        Coordinate current = startingPoint;
        Set<Coordinate> visited = new HashSet<>();

        while (true) {
            visited.add(current);

            Coordinate next = current.move(direction);
            char ch = next.at(map, 'G');
            if (ch == 'G') {
                break;
            } else if (ch == '#') {
                direction = switch (direction) {
                    case UP -> Direction.RIGHT;
                    case RIGHT -> Direction.DOWN;
                    case DOWN -> Direction.LEFT;
                    case LEFT -> Direction.UP;
                };
            } else {
                current = next;
            }
        }

        return visited.size(); // Your puzzle answer was 4883
    }
}
