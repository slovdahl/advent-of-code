package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;
import lib.Pair;
import lib.Parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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

        applyMoves(position, map);

        return Matrix.findChars(map, 'O')
                .stream()
                .mapToLong(c -> c.row() * 100L + c.column())
                .sum(); // Your puzzle answer was 1429911
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        growMap();
        Coordinate position = Matrix.findChar(map, '@');

        applyMoves(position, map);

        Matrix.print(System.out, map);

        return Matrix.findChars(map, '[')
                .stream()
                .mapToLong(c -> c.row() * 100L + c.column())
                .sum(); // Your puzzle answer was XXXX
        // 1434730 too low
    }

    private void applyMoves(Coordinate position, char[][] map) {
        // TODO: clean up once debugging is done
        char[] charArray = moves.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            position = switch (c) {
                case '^' -> moveToNextPosition(position, map, Direction.UP);
                case '>' -> moveToNextPosition(position, map, Direction.RIGHT);
                case 'v' -> moveToNextPosition(position, map, Direction.DOWN);
                case '<' -> moveToNextPosition(position, map, Direction.LEFT);
                default -> throw new IllegalStateException();
            };

            for (Coordinate ch : Matrix.findChars(map, '[')) {
                if (ch.moveRight().at(map) != ']') {
                    throw new IllegalStateException();
                }
            }
        }
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
        Map<Coordinate, Integer> sidewaysStartingPoints = new HashMap<>();
        Pair<Boolean, Integer> result = canShiftOneStep(robotPosition, direction, map, sidewaysStartingPoints, new HashSet<>());
        if (!result.first()) {
            return robotPosition;
        }

        shift(map, robotPosition, direction, result.second());

        for (var entry : sidewaysStartingPoints.entrySet()) {
            shift(map, entry.getKey(), direction, entry.getValue());
        }

        return robotPosition.move(direction);
    }

    private static Pair<Boolean, Integer> canShiftOneStep(Coordinate from,
                                                          Direction direction,
                                                          char[][] map,
                                                          Map<Coordinate, Integer> sidewaysStartingPoints,
                                                          Set<Integer> checkedColumns) {

        checkedColumns.add(from.column());

        char ch = from.atDirection(map, direction, '#');
        Coordinate current = from.move(direction);
        int toMove = 0;

        /*
        TODO: Handle this case, Direction.DOWN:

        ....[].....
        ...[].@....
        .....[].##.
        ....[][]...
        ##..[].....
        []..[]##[].
        [].[][].[].
        ....[].....
        ..........#
         */
        while (ch != '#') {
            if (ch == '.') {
                return Pair.of(true, toMove);
            } else if (ch == 'O') {
                toMove++;
            } else if (ch == '[' || ch == ']') {
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    toMove++;

                    Direction checkDirection;
                    if (ch == ']') {
                        checkDirection = Direction.LEFT;
                    } else {
                        checkDirection = Direction.RIGHT;
                    }

                    Coordinate move = current.move(checkDirection);
                    if (!checkedColumns.contains(move.column())) {
                        Pair<Boolean, Integer> canMoveOneStep = canShiftOneStep(
                                move,
                                direction,
                                map,
                                sidewaysStartingPoints,
                                checkedColumns
                        );

                        if (!canMoveOneStep.first()) {
                            return Pair.of(false, 0);
                        }

                        sidewaysStartingPoints.put(move, canMoveOneStep.second());
                    }
                } else {
                    toMove++;
                }
            } else {
                throw new IllegalStateException();
            }

            current = current.move(direction);
            ch = current.at(map);
        }

        return Pair.of(false, 0);
    }

    private static void shift(char[][] map, Coordinate from, Direction direction, int n) {
        Coordinate freeSpot = from.move(direction, n + 1);
        for (int i = 0; i <= n; i++) {
            Coordinate next = freeSpot.move(direction.opposite());
            char newChar = next.at(map);
            freeSpot.set(map, newChar);
            freeSpot = next;
        }
        from.set(map, '.');
    }
}
