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

    private char[][] map;
    private String moves;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);
        map = Matrix.matrix(sections.getFirst());
        moves = String.join("", sections.getLast());
    }

    @Override
    protected Object part1(Stream<String> input) {
        char[][] map = Matrix.deepClone(this.map);
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
                .sum(); // Your puzzle answer was 1429911
    }

    private static Coordinate moveToNextPosition(Coordinate robotPosition, char[][] map, Direction direction) {
        char ch = robotPosition.atDirection(map, direction, '#');
        if (ch == '#') {
            return robotPosition;
        }

        Coordinate newPosition = robotPosition.move(direction);
        Coordinate current = newPosition;
        Coordinate freeSpot = null;
        int boxes = 0;

        while (ch != '#') {
            if (ch == '.') {
                freeSpot = current;
                break;
            } else if (ch == 'O') {
                boxes++;
            } else {
                throw new IllegalStateException();
            }

            current = current.move(direction);
            ch = current.at(map);
        }

        if (freeSpot != null) {
            for (int i = 0; i < boxes; i++) {
                freeSpot.set(map, 'O');
                freeSpot = freeSpot.move(direction.opposite());
            }

            newPosition.set(map, '@');
            robotPosition.set(map, '.');

            return newPosition;
        } else {
            return robotPosition;
        }
    }
}
