package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;
import lib.QuadFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day21 extends Day {

    private List<String> codes;
    private char[][] directionalKeypad;
    private char[][] numericKeypad;
    private Coordinate directionalInvalidPoint;
    private Coordinate directionalStart;
    private Coordinate numericInvalidPoint;
    private Coordinate numericStart;
    private Map<Character, Coordinate> directionCharacterPositions;
    private QuadFunction<CharMatrix, Direction, Coordinate, Coordinate, Integer> costFunction;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        codes = input.toList();

        /*
            +---+---+
            | ^ | A |
        +---+---+---+
        | < | v | > |
        +---+---+---+
         */
        directionalKeypad = new char[][]{
                new char[]{'X', '^', 'A'},
                new char[]{'<', 'v', '>'}
        };

        /*
        +---+---+---+
        | 7 | 8 | 9 |
        +---+---+---+
        | 4 | 5 | 6 |
        +---+---+---+
        | 1 | 2 | 3 |
        +---+---+---+
            | 0 | A |
            +---+---+
         */
        numericKeypad = new char[][]{
                new char[]{'7', '8', '9'},
                new char[]{'4', '5', '6'},
                new char[]{'1', '2', '3'},
                new char[]{'X', '0', 'A'}
        };

        directionalInvalidPoint = Matrix.findChar(directionalKeypad, 'X');
        directionalStart = Matrix.findChar(directionalKeypad, 'A');
        numericInvalidPoint = Matrix.findChar(numericKeypad, 'X');
        numericStart = Matrix.findChar(numericKeypad, 'A');

        directionCharacterPositions = Map.of(
                '^', Matrix.findChar(directionalKeypad, '^'),
                'A', Matrix.findChar(directionalKeypad, 'A'),
                '<', Matrix.findChar(directionalKeypad, '<'),
                'v', Matrix.findChar(directionalKeypad, 'v'),
                '>', Matrix.findChar(directionalKeypad, '>')
        );

        costFunction = (charMatrix, direction, current, next) -> {
            if (direction != null && current.directionTo(next) == direction) {
                return 1;
            } else {
                return 2;
            }
        };
    }

    @Override
    protected Object part1(Stream<String> input) {
        Coordinate numericPosition = numericStart;
        Coordinate directionalPosition1 = directionalStart;
        Coordinate directionalPosition2 = directionalStart;

        Map<String, Integer> result = new HashMap<>();

        for (String code : codes) {
            List<Character> numericKeypadMoves = new ArrayList<>();

            for (char c : code.toCharArray()) {
                if (numericPosition.at(numericKeypad) == c) {
                    numericKeypadMoves.add('A');
                    continue;
                }
                Coordinate target = Matrix.findChar(numericKeypad, c);
                var numpadDijkstra = new Dijkstra<>(
                        new CharMatrix(numericKeypad, coordinate -> !coordinate.equals(numericInvalidPoint)),
                        numericPosition,
                        target,
                        costFunction
                );

                numpadDijkstra.findShortestPath().orElseThrow();
                List<Coordinate> lowestCostPath = numpadDijkstra.getLowestCostPath();
                List<Character> directionChars = toDirectionChars(Coordinate.toDirections(lowestCostPath));
                numericKeypadMoves.addAll(directionChars);
                numericKeypadMoves.add('A');

                numericPosition = target;
            }

            List<Character> directionalMoves1 = new ArrayList<>();
            for (Character directionChar : numericKeypadMoves) {
                if (directionalPosition1.at(directionalKeypad) == directionChar) {
                    directionalMoves1.add('A');
                    continue;
                }

                Coordinate targetDirectionChar = directionCharacterPositions.get(directionChar);
                var directionalDijkstra = new Dijkstra<>(
                        new CharMatrix(directionalKeypad, coordinate -> !coordinate.equals(directionalInvalidPoint)),
                        directionalPosition1,
                        targetDirectionChar,
                        costFunction
                );

                directionalDijkstra.findShortestPath().orElseThrow();
                List<Coordinate> lowestCostPath = directionalDijkstra.getLowestCostPath();
                List<Character> directionChars = toDirectionChars(Coordinate.toDirections(lowestCostPath));
                directionalMoves1.addAll(directionChars);
                directionalMoves1.add('A');

                directionalPosition1 = targetDirectionChar;
            }

            List<Character> directionalMoves2 = new ArrayList<>();
            for (Character directionChar : directionalMoves1) {
                if (directionalPosition2.at(directionalKeypad) == directionChar) {
                    directionalMoves2.add('A');
                    continue;
                }

                Coordinate targetDirectionChar = directionCharacterPositions.get(directionChar);
                var directionalDijkstra = new Dijkstra<>(
                        new CharMatrix(directionalKeypad, coordinate -> !coordinate.equals(directionalInvalidPoint)),
                        directionalPosition2,
                        targetDirectionChar,
                        costFunction
                );

                directionalDijkstra.findShortestPath().orElseThrow();
                List<Coordinate> lowestCostPath = directionalDijkstra.getLowestCostPath();
                List<Character> directionChars = toDirectionChars(Coordinate.toDirections(lowestCostPath));
                directionalMoves2.addAll(directionChars);
                directionalMoves2.add('A');

                directionalPosition2 = targetDirectionChar;
            }

            result.put(code, directionalMoves2.size());
        }

        return result.entrySet().stream()
                .mapToLong(e -> Long.parseLong(e.getKey().substring(0, 3)) * e.getValue())
                .sum(); // Your puzzle answer was 211930
    }

    private static List<Character> toDirectionChars(List<Direction> directions) {
        return directions.stream()
                .map(Direction::toChar)
                .toList();
    }
}
