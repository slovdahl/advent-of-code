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
        return Mode.SAMPLE_INPUT;
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
                .sum();
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        growMap();

        Coordinate position = Matrix.findChar(map, '@');


        for (char c : moves.toCharArray()) {
            position = switch (c) {
                case '^' -> moveToNextPosition2(position, map, Direction.UP);
                case '>' -> moveToNextPosition2(position, map, Direction.RIGHT);
                case 'v' -> moveToNextPosition2(position, map, Direction.DOWN);
                case '<' -> moveToNextPosition2(position, map, Direction.LEFT);
                default -> throw new IllegalStateException();
            };
        }

        Matrix.print(System.out, map);

        return Matrix.findChars(map, '[')
                .stream()
                .mapToLong(c -> c.row() * 100L + c.column())
                .sum();
    }

    private void growMap() {
        for (int i = 0; i < map.length; i++) {
            char[] oldRow = map[i];
            map[i] = new char[oldRow.length * 2];
            for (int j = 0; j < oldRow.length; j++) {
                switch (oldRow[j]) {
                    case '#' -> {
                        map[i][j * 2] = '#';
                        map[i][j * 2 + 1] = '#';
                    }

                    case 'O' -> {
                        map[i][j * 2] = '[';
                        map[i][j * 2 + 1] = ']';
                    }

                    case '.' -> {
                        map[i][j * 2] = '.';
                        map[i][j * 2 + 1] = '.';
                    }

                    case '@' -> {
                        map[i][j * 2] = '@';
                        map[i][j * 2 + 1] = '.';
                    }
                }
            }
        }
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
            } else if (ch == '[' || ch == ']') { // TODO: need to rewrite this completely
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

    private static Coordinate moveToNextPosition2(Coordinate robotPosition, char[][] map, Direction direction) {
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
