package year2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
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
                direction = direction.turnRight();
            } else {
                current = next;
            }
        }

        return visited.size(); // Your puzzle answer was 4883
    }

    @Override
    protected Object part2(Stream<String> input) {
        char[][] map = Matrix.matrix(input.toList());

        Coordinate startingPoint = Matrix.findChar(map, '^');
        Direction direction = Direction.UP;

        Coordinate current = startingPoint;
        Set<Coordinate> visited = new HashSet<>();

        while (true) {
            visited.add(current);
            current.set(map, 'X');

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

        Matrix.print(System.out, map);

        int obstaclePositions = 0;
        for (Coordinate coordinate : visited) {
            if (coordinate.equals(startingPoint)) {
                continue;
            }

            char[][] mapCopy = Matrix.deepClone(map);

            coordinate.set(mapCopy, '#');

            Direction direction2 = Direction.UP;
            Coordinate current2 = startingPoint;
            Multiset<Coordinate> visited2 = HashMultiset.create();

            while (true) {
                visited2.add(current2);
                if (visited2.count(current2) > 20) {
                    obstaclePositions++;
                    break;
                }

                Coordinate next = current2.move(direction2);
                char ch = next.at(mapCopy, 'G');
                if (ch == 'G') {
                    break;
                } else if (ch == '#') {
                    direction2 = direction2.turnRight();
                } else {
                    current2 = next;
                }
            }
        }

        return obstaclePositions; // Your puzzle answer was 1655
    }
}
