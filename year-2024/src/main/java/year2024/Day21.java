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
        return countSteps(2);
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return countSteps(25);
        // 119864263901146 too low
        // 119864263901147 too low
        // 300043033609508 too high
        // 303327203995280 incorrect

        /*
        With robots = 25
        Code: 985A move ^ steps 10064045256
        Code: 985A move ^ steps 10064045257
        Code: 985A move ^ steps 10064045258
        Code: 985A move A steps 16566083588
        Code: 985A move < steps 30244422776
         */

        /*
        With robots = 25
        Code: 985A move ^ steps 10064045256 total 10064045256
        Code: 985A move ^ steps 1 total 10064045257
        Code: 985A move ^ steps 1 total 10064045258
        Code: 985A move A steps 6502038330 total 16566083588
        Code: 985A move < steps 13678339188 total 30244422776
        Code: 985A move A steps 11513885508 total 41758308284
        Code: 985A move v steps 12661547417 total 54419855701
        Code: 985A move A steps 11513885507 total 65933741208
        Code: 985A move v steps 12661547417 total 78595288625
        Code: 985A move v steps 1 total 78595288626
        Code: 985A move > steps 6502038330 total 85097326956
        Code: 985A move A steps 6617992330 total 91715319286
        Code: 985A, steps: 91715319286
        Code: 540A move < steps 13678339188 total 13678339188
        Code: 540A move ^ steps 11513885507 total 25192224695
        Code: 540A move ^ steps 1 total 25192224696
        Code: 540A move A steps 6502038330 total 31694263026
        Code: 540A move < steps 13678339188 total 45372602214
        Code: 540A move A steps 11513885508 total 56886487722
        Code: 540A move > steps 9657849018 total 66544336740
        Code: 540A move v steps 10064045256 total 76608381996
        Code: 540A move v steps 1 total 76608381997
        Code: 540A move A steps 11513885507 total 88122267504
        Code: 540A move > steps 9657849018 total 97780116522
        Code: 540A move A steps 6617992330 total 104398108852
        Code: 540A, steps: 104398108852
        Code: 463A move ^ steps 10064045256 total 10064045256
        Code: 463A move ^ steps 1 total 10064045257
        Code: 463A move < steps 13678339187 total 23742384444
        Code: 463A move < steps 1 total 23742384445
        Code: 463A move A steps 11513885508 total 35256269953
        Code: 463A move > steps 9657849018 total 44914118971
        Code: 463A move > steps 1 total 44914118972
        Code: 463A move A steps 6617992330 total 51532111302
        Code: 463A move v steps 12661547417 total 64193658719
        Code: 463A move A steps 11513885507 total 75707544226
        Code: 463A move v steps 12661547417 total 88369091643
        Code: 463A move A steps 11513885507 total 99882977150
        Code: 463A, steps: 99882977150
        Code: 671A move ^ steps 10064045256 total 10064045256
        Code: 671A move ^ steps 1 total 10064045257
        Code: 671A move A steps 6502038330 total 16566083587
        Code: 671A move < steps 13678339188 total 30244422775
        Code: 671A move < steps 1 total 30244422776
        Code: 671A move ^ steps 11513885507 total 41758308283
        Code: 671A move A steps 6502038330 total 48260346613
        Code: 671A move v steps 12661547417 total 60921894030
        Code: 671A move v steps 1 total 60921894031
        Code: 671A move A steps 11513885507 total 72435779538
        Code: 671A move > steps 9657849018 total 82093628556
        Code: 671A move > steps 1 total 82093628557
        Code: 671A move v steps 10064045256 total 92157673813
        Code: 671A move A steps 11513885507 total 103671559320
        Code: 671A, steps: 103671559320
        Code: 382A move ^ steps 10064045256 total 10064045256
        Code: 382A move A steps 6502038330 total 16566083586
        Code: 382A move < steps 13678339188 total 30244422774
        Code: 382A move ^ steps 11513885507 total 41758308281
        Code: 382A move ^ steps 1 total 41758308282
        Code: 382A move A steps 6502038330 total 48260346612
        Code: 382A move v steps 12661547417 total 60921894029
        Code: 382A move v steps 1 total 60921894030
        Code: 382A move A steps 11513885507 total 72435779537
        Code: 382A move v steps 12661547417 total 85097326954
        Code: 382A move > steps 6502038330 total 91599365284
        Code: 382A move A steps 6617992330 total 98217357614
        Code: 382A, steps: 98217357614

        -> Complexity = 300043033609508

        Parallelized:
        ==========================================================
         Part    2
         Time    23.70 min
         Result  300043033609508
        ==========================================================
         */
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

        dijkstra.findShortestPath().orElseThrow();
        List<Coordinate> lowestCostPath = dijkstra.getLowestCostPath();

        List<Character> directionChars = toDirectionChars(Coordinate.toDirections(lowestCostPath));
        directionChars.add('A');

        positions.set(n - 1, targetDirectionChar);

        long steps = 0;
        for (Character ch : directionChars) {
            steps += recurse(n - 1, ch, positions);
        }

        return steps;
    }

    private static List<Character> toDirectionChars(List<Direction> directions) {
        return directions.stream()
                .map(Direction::toChar)
                .collect(toList());
    }
}
