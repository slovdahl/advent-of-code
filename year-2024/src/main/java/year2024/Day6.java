package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day6 extends Day {

    private char[][] map;
    private Coordinate startingPoint;
    private Set<Coordinate> visited;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());
        startingPoint = Matrix.findChar(map, '^');

        Direction direction = Direction.UP;

        Coordinate current = startingPoint;
        visited = new HashSet<>();

        while (true) {
            visited.add(current);

            Coordinate next = current.move(direction);
            char ch = next.at(map, 'G');
            if (ch == 'G') {
                break;
            } else if (ch == '#') {
                direction = direction.turnRight();
            } else {
                current = next;
            }
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        return visited.size(); // Your puzzle answer was 4883
    }

    @Override
    protected Object part2(Stream<String> input) {
        // We can't put an obstacle at the starting point
        visited.remove(startingPoint);

        int obstaclePositions = 0;

        for (Coordinate coordinate : visited) {
            char old = coordinate.set(map, '#');

            Direction direction = Direction.UP;
            Coordinate current = startingPoint;
            SetMultimap<Coordinate, Direction> visitedDirections = HashMultimap.create();

            while (true) {
                if (!visitedDirections.put(current, direction)) {
                    obstaclePositions++;
                    break;
                }

                Coordinate next = current.move(direction);
                char ch = next.at(map, 'G');
                if (ch == 'G') {
                    break;
                } else if (ch == '#') {
                    direction = direction.turnRight();
                } else {
                    current = next;
                }
            }

            coordinate.set(map, old);
        }

        return obstaclePositions; // Your puzzle answer was 1655
    }
}
