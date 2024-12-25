package year2024;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;
import lib.Pair;
import lib.QuadFunction;
import lib.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings("unused")
public class Day21 extends Day {

    private List<String> codes;
    private char[][] directionalKeypad;
    private Coordinate directionalInvalidPoint;
    private Coordinate directionalStart;
    private Map<String, Map<Character, List<String>>> numericKeypadMovesPerCode;
    private QuadFunction<CharMatrix, Direction, Coordinate, Coordinate, Integer> costFunction;
    private Cache<Triple<Integer, Character, List<Coordinate>>, Pair<Long, List<Coordinate>>> cache;

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
        char[][] numericKeypad = new char[][]{
                new char[]{'7', '8', '9'},
                new char[]{'4', '5', '6'},
                new char[]{'1', '2', '3'},
                new char[]{'X', '0', 'A'}
        };

        directionalInvalidPoint = Matrix.findChar(directionalKeypad, 'X');
        directionalStart = Matrix.findChar(directionalKeypad, 'A');

        costFunction = (charMatrix, direction, current, next) -> {
            if (direction != null && current.directionTo(next) == direction) {
                return 1;
            } else {
                return 2;
            }
        };

        numericKeypadMovesPerCode = new HashMap<>();
        Coordinate numericPosition = Matrix.findChar(numericKeypad, 'A');
        Coordinate numericInvalidPoint = Matrix.findChar(numericKeypad, 'X');

        for (String code : codes) {
            Map<Character, List<String>> numericKeypadMoves = new LinkedHashMap<>();

            for (char c : code.toCharArray()) {
                if (numericPosition.at(numericKeypad) == c) {
                    numericKeypadMoves.computeIfAbsent(c, _ -> new ArrayList<>())
                            .add("A");
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

                for (List<Coordinate> lowestCostPath : numpadDijkstra.getAllLowestCostPaths()) {
                    List<Character> moves = toDirectionChars(Coordinate.toDirections(lowestCostPath));
                    moves.add('A');

                    numericKeypadMoves.computeIfAbsent(c, _ -> new ArrayList<>())
                            .add(moves.stream().map(String::valueOf).collect(joining()));
                }

                numericPosition = target;
            }

            numericKeypadMovesPerCode.put(code, numericKeypadMoves);
        }

        cache = Caffeine.newBuilder()
                .build();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return countSteps(2); // Your puzzle answer was 211930
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return countSteps(25); // Your puzzle answer was 263492840501566
    }

    private long countSteps(int keypadsUsedByRobots) {
        Map<String, Long> result = codes.stream()
                .map(code -> {
                    List<Coordinate> robotPositions = IntStream.range(0, keypadsUsedByRobots)
                            .mapToObj(_ -> directionalStart)
                            .collect(toList());

                    long steps = 0;

                    for (var entry : numericKeypadMovesPerCode.get(code).entrySet()) {
                        Character numericKeypadKey = entry.getKey();
                        List<String> candidatesForMove = entry.getValue();

                        long lowestSteps = Long.MAX_VALUE;

                        for (String candidate : candidatesForMove) {
                            long candidateSteps = 0L;
                            for (char numericKeypadMove : candidate.toCharArray()) {
                                candidateSteps += recurse(keypadsUsedByRobots, numericKeypadMove, robotPositions);
                            }
                            lowestSteps = Math.min(lowestSteps, candidateSteps);
                        }

                        steps += lowestSteps;
                    }

                    return Pair.of(code, steps);
                })
                .collect(toMap(Pair::first, Pair::second));

        return result.entrySet().stream()
                .mapToLong(e -> Long.parseLong(e.getKey().substring(0, 3)) * e.getValue())
                .sum();
    }

    private long recurse(int n, Character move, List<Coordinate> positions) {
        // Cannot use LoadingCache or cache.get(key, function) because of the recursive nature
        // of the algorithm.
        List<Coordinate> positionsForN = List.copyOf(positions.subList(0, n));
        Triple<Integer, Character, List<Coordinate>> cacheKey = Triple.of(n, move, positionsForN);
        Pair<Long, List<Coordinate>> result = cache.getIfPresent(cacheKey);

        long steps;
        List<Coordinate> updatedPositions;
        if (result != null) {
            steps = result.first();
            updatedPositions = result.second();
        } else {
            updatedPositions = new ArrayList<>(positionsForN);
            steps = doRecurse(n, move, updatedPositions);
            cache.put(cacheKey, Pair.of(steps, List.copyOf(updatedPositions)));
        }

        for (int i = 0; i < updatedPositions.size(); i++) {
            positions.set(i, updatedPositions.get(i));
        }

        return steps;
    }

    private long doRecurse(int n, Character move, List<Coordinate> positions) {
        if (move == null) {
            throw new IllegalArgumentException("Move must not be null");
        }
        if (n == 0) {
            return 1L;
        }

        Coordinate position = positions.get(n - 1);

        if (position.at(directionalKeypad) == move) {
            return recurse(n - 1, 'A', positions);
        }

        Coordinate targetDirectionChar = Matrix.findChar(directionalKeypad, move);
        var dijkstra = new Dijkstra<>(
                new CharMatrix(directionalKeypad, coordinate -> !coordinate.equals(directionalInvalidPoint)),
                position,
                targetDirectionChar,
                costFunction
        );

        dijkstra.findAllShortestPaths().orElseThrow();

        long lowestSteps = Long.MAX_VALUE;

        for (List<Coordinate> lowestCostPath : dijkstra.getAllLowestCostPaths()) {
            List<Character> directionChars = toDirectionChars(Coordinate.toDirections(lowestCostPath));
            directionChars.add('A');

            long steps = 0;
            for (Character ch : directionChars) {
                steps += recurse(n - 1, ch, positions);
            }

            lowestSteps = Math.min(lowestSteps, steps);
        }

        positions.set(n - 1, targetDirectionChar);

        return lowestSteps;
    }

    private static List<Character> toDirectionChars(List<Direction> directions) {
        return directions.stream()
                .map(Direction::toChar)
                .collect(toList());
    }
}
