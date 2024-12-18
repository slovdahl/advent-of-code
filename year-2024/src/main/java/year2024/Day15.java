package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;
import lib.Parse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

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

        return Matrix.findChars(map, '[')
                .stream()
                .mapToLong(c -> c.row() * 100L + c.column())
                .sum(); // Your puzzle answer was 1453087
    }

    private void applyMoves(Coordinate position, char[][] map) {
        for (char c : moves.toCharArray()) {
            position = switch (c) {
                case '^' -> moveToNextPosition(position, map, Direction.UP);
                case '>' -> moveToNextPosition(position, map, Direction.RIGHT);
                case 'v' -> moveToNextPosition(position, map, Direction.DOWN);
                case '<' -> moveToNextPosition(position, map, Direction.LEFT);
                default -> throw new IllegalStateException();
            };

            if (position.at(map) != '@') {
                throw new IllegalStateException("Unexpected robot position at " + position);
            }

            for (Coordinate coordinate : Matrix.findChars(map, '[')) {
                if (coordinate.moveRight().at(map) != ']') {
                    throw new IllegalStateException("Broken map at " + coordinate);
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
        TreeMap<Coordinate, Coordinate> movesToMake = new TreeMap<>(switch (direction) {
            case UP -> comparing(Coordinate::row).thenComparing(Coordinate::column);
            case RIGHT -> comparing(Coordinate::column).reversed().thenComparing(Coordinate::row);
            case DOWN -> comparing(Coordinate::row).reversed().thenComparing(Coordinate::column);
            case LEFT -> comparing(Coordinate::column).thenComparing(Coordinate::row);
        });

        Set<Coordinate> visited = new HashSet<>();
        shiftOneStep(robotPosition, direction, map, visited, movesToMake);

        if (!movesToMake.isEmpty()) {
            for (var entry : movesToMake.entrySet()) {
                Coordinate from = entry.getKey();
                Coordinate to = entry.getValue();
                char oldTo = to.at(map);
                to.set(map, from.at(map));
                from.set(map, oldTo);
            }

            return robotPosition.move(direction);
        } else {
            return robotPosition;
        }
    }

    private static void shiftOneStep(Coordinate from,
                                     Direction direction,
                                     char[][] map,
                                     Set<Coordinate> visited,
                                     TreeMap<Coordinate, Coordinate> movesToMake) {

        if (visited.contains(from)) {
            return;
        }

        visited.add(from);

        char ch = from.atDirection(map, direction, '#');
        Coordinate previous = from;
        Coordinate current = from.move(direction);

        while (ch != '#') {
            if (ch == '.') {
                movesToMake.put(previous, current);
                return;
            } else if (ch == 'O') {
                // Part 1
                movesToMake.put(previous, current);
            } else if (ch == '[' || ch == ']') {
                // Part 2
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    TreeMap<Coordinate, Coordinate> sidewaysMovesToMake = new TreeMap<>(movesToMake.comparator());
                    Coordinate move = current.move(ch == ']' ? Direction.LEFT : Direction.RIGHT);
                    if (!visited.contains(move)) {
                        shiftOneStep(move, direction, map, visited, sidewaysMovesToMake);

                        if (sidewaysMovesToMake.isEmpty()) {
                            // Shifting not possible, the sideways move was blocked.
                            movesToMake.clear();
                            return;
                        }

                        movesToMake.put(previous, current);
                        movesToMake.putAll(sidewaysMovesToMake);
                    }
                } else {
                    movesToMake.put(previous, current);
                }
            } else {
                throw new IllegalStateException("Unexpected character " + ch + " at position " + current);
            }

            previous = current;
            current = current.move(direction);
            ch = current.at(map);
        }

        // Shifting not possible, the move was blocked.
        movesToMake.clear();
    }
}
