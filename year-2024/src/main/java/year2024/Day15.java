package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;
import lib.Parse;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("unused")
public class Day15 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);
        char[][] map = Matrix.matrix(sections.getFirst());
        String moves = String.join("", sections.getLast());

        Coordinate position = Matrix.findChar(map, '@');
        checkNotNull(position);

        for (char c : moves.toCharArray()) {
            position = switch (c) {
                case '^' -> moveToNextPosition(position, map, Direction.UP);
                case '>' -> moveToNextPosition(position, map, Direction.RIGHT);
                case 'v' -> moveToNextPosition(position, map, Direction.DOWN);
                case '<' -> moveToNextPosition(position, map, Direction.LEFT);
                default -> throw new IllegalStateException();
            };
        }

        return Matrix.findChars(map, 'O')
                .stream()
                .mapToLong(c -> c.row() * 100L + c.column())
                .sum();
    }

    private static Coordinate moveToNextPosition(Coordinate robotPosition, char[][] map, Direction direction) {
        char ch = robotPosition.atDirection(map, direction, '#');
        if (ch == '#') {
            return robotPosition;
        }

        Coordinate newPosition = robotPosition.move(direction);
        Coordinate current = newPosition;
        Coordinate lastFreeSpot = null;
        int boxes = 0;

        while (ch != '#') {
            if (ch == '.') {
                lastFreeSpot = current;
                break;
            } else if (ch == 'O') {
                boxes++;
            } else {
                throw new IllegalStateException();
            }

            current = current.move(direction);
            ch = current.at(map);
        }

        if (lastFreeSpot != null) {
            for (int i = 0; i < boxes; i++) {
                lastFreeSpot.set(map, 'O');
                lastFreeSpot = lastFreeSpot.move(direction.opposite());
            }

            newPosition.set(map, '@');
            robotPosition.set(map, '.');

            return newPosition;
        } else {
            return robotPosition;
        }
    }
}
