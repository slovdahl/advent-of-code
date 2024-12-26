package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;
import lib.Pair;
import lib.QuadFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class Day21 extends Day {

    private List<String> codes;
    private char[][] directionalKeypad;
    private Coordinate directionalInvalidPoint;
    private Coordinate directionalStart;
    private Map<String, List<Character>> numericKeypadMovesPerCode;
    private QuadFunction<CharMatrix, Direction, Coordinate, Coordinate, Integer> costFunction;
    private Map<Pair<Coordinate, Character>, Pair<Coordinate, List<Character>>> movesCache;

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
                return 3;
            }
        };

        numericKeypadMovesPerCode = new HashMap<>();
        Coordinate numericPosition = Matrix.findChar(numericKeypad, 'A');
        Coordinate numericInvalidPoint = Matrix.findChar(numericKeypad, 'X');

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
                numericKeypadMoves.addAll(toDirectionChars(Coordinate.toDirections(lowestCostPath)));
                numericKeypadMoves.add('A');

                numericPosition = target;
            }

            numericKeypadMovesPerCode.put(code, List.copyOf(numericKeypadMoves));
        }

        movesCache = new HashMap<>();
    }

    @Override
    protected Object part1(Stream<String> input) {
        int keypadsUsedByRobots = 2;

        List<Coordinate> robotPositions = IntStream.range(0, keypadsUsedByRobots)
                .mapToObj(_ -> directionalStart)
                .collect(toList());

        Map<String, Long> result = new HashMap<>();

        for (String code : codes) {
            long steps = 0;
            for (Character numericKeypadMove : numericKeypadMovesPerCode.get(code)) {
                steps += recurse(keypadsUsedByRobots, numericKeypadMove, robotPositions);
            }

            result.put(code, steps);
        }

        return result.entrySet().stream()
                .mapToLong(e -> Long.parseLong(e.getKey().substring(0, 3)) * e.getValue())
                .sum(); // Your puzzle answer was 211930
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        int keypadsUsedByRobots = 25;

        List<Coordinate> robotPositions = IntStream.range(0, keypadsUsedByRobots)
                .mapToObj(_ -> directionalStart)
                .collect(toList());

        // Moves after an A press on the numeric keypad will always be the same,
        // because the directional keypads are all lined up on A.
        Map<Character, Pair<Long, List<Coordinate>>> firstMoveCache = new HashMap<>();

        Map<String, Long> result = new HashMap<>();

        for (String code : codes) {
            long steps = 0;
            char previous = 'X';
            for (Character numericKeypadMove : numericKeypadMovesPerCode.get(code)) {
                Pair<Long, List<Coordinate>> cachedSteps = null;
                if (previous == 'A') {
                    cachedSteps = firstMoveCache.get(numericKeypadMove);
                }

                long moveSteps;
                if (cachedSteps != null) {
                    moveSteps = cachedSteps.first();
                    robotPositions = cachedSteps.second();
                } else {
                    moveSteps = recurse(keypadsUsedByRobots, numericKeypadMove, robotPositions);
                }

                steps += moveSteps;
                System.out.println("Code: " + code + " move " + numericKeypadMove + " steps " + moveSteps + " total " + steps);
                if (cachedSteps == null && (previous == 'A' || previous == 'X')) {
                    firstMoveCache.put(numericKeypadMove, Pair.of(moveSteps, new ArrayList<>(robotPositions)));
                }

                previous = numericKeypadMove;
            }

            System.out.println("Code: " + code + ", steps: " + steps);
            result.put(code, steps);
        }

        return result.entrySet().stream()
                .mapToLong(e -> Long.parseLong(e.getKey().substring(0, 3)) * e.getValue())
                .sum(); // Your puzzle answer was XXX
        // 119864263901146 too low
        // 119864263901147 too low
        // 300043033609508 too high

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
         */

        /*
        With robots = 24
        Code: 985A move ^ steps 4020490168 total 4020490168
        Code: 985A move ^ steps 1 total 4020490169
        Code: 985A move ^ steps 1 total 4020490170
        Code: 985A move A steps 2597502162 total 6617992332
        Code: 985A move < steps 5464362368 total 12082354700
        Code: 985A move A steps 4599682888 total 16682037588
        Code: 985A move v steps 5058166131 total 21740203719
        Code: 985A move A steps 4599682887 total 26339886606
        Code: 985A move v steps 5058166131 total 31398052737
        Code: 985A move v steps 1 total 31398052738
        Code: 985A move > steps 2597502162 total 33995554900
        Code: 985A move A steps 2643821116 total 36639376016
        Code: 985A, steps: 36639376016
        Code: 540A move < steps 5464362368 total 5464362368
        Code: 540A move ^ steps 4599682887 total 10064045255
        Code: 540A move ^ steps 1 total 10064045256
        Code: 540A move A steps 2597502162 total 12661547418
        Code: 540A move < steps 5464362368 total 18125909786
        Code: 540A move A steps 4599682888 total 22725592674
        Code: 540A move > steps 3858217214 total 26583809888
        Code: 540A move v steps 4020490168 total 30604300056
        Code: 540A move v steps 1 total 30604300057
        Code: 540A move A steps 4599682887 total 35203982944
        Code: 540A move > steps 3858217214 total 39062200158
        Code: 540A move A steps 2643821116 total 41706021274
        Code: 540A, steps: 41706021274
        Code: 463A move ^ steps 4020490168 total 4020490168
        Code: 463A move ^ steps 1 total 4020490169
        Code: 463A move < steps 5464362367 total 9484852536
        Code: 463A move < steps 1 total 9484852537
        Code: 463A move A steps 4599682888 total 14084535425
        Code: 463A move > steps 3858217214 total 17942752639
        Code: 463A move > steps 1 total 17942752640
        Code: 463A move A steps 2643821116 total 20586573756
        Code: 463A move v steps 5058166131 total 25644739887
        Code: 463A move A steps 4599682887 total 30244422774
        Code: 463A move v steps 5058166131 total 35302588905
        Code: 463A move A steps 4599682887 total 39902271792
        Code: 463A, steps: 39902271792
        Code: 671A move ^ steps 4020490168 total 4020490168
        Code: 671A move ^ steps 1 total 4020490169
        Code: 671A move A steps 2597502162 total 6617992331
        Code: 671A move < steps 5464362368 total 12082354699
        Code: 671A move < steps 1 total 12082354700
        Code: 671A move ^ steps 4599682887 total 16682037587
        Code: 671A move A steps 2597502162 total 19279539749
        Code: 671A move v steps 5058166131 total 24337705880
        Code: 671A move v steps 1 total 24337705881
        Code: 671A move A steps 4599682887 total 28937388768
        Code: 671A move > steps 3858217214 total 32795605982
        Code: 671A move > steps 1 total 32795605983
        Code: 671A move v steps 4020490168 total 36816096151
        Code: 671A move A steps 4599682887 total 41415779038
        Code: 671A, steps: 41415779038
        Code: 382A move ^ steps 4020490168 total 4020490168
        Code: 382A move A steps 2597502162 total 6617992330
        Code: 382A move < steps 5464362368 total 12082354698
        Code: 382A move ^ steps 4599682887 total 16682037585
        Code: 382A move ^ steps 1 total 16682037586
        Code: 382A move A steps 2597502162 total 19279539748
        Code: 382A move v steps 5058166131 total 24337705879
        Code: 382A move v steps 1 total 24337705880
        Code: 382A move A steps 4599682887 total 28937388767
        Code: 382A move v steps 5058166131 total 33995554898
        Code: 382A move > steps 2597502162 total 36593057060
        Code: 382A move A steps 2643821116 total 39236878176
        Code: 382A, steps: 39236878176
        ==========================================================
         Part    2
         Time    42.26 min
         Result  119864263901146
        ==========================================================
         */
    }

    private long recurse(int n, Character move, List<Coordinate> positions) {
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

        Pair<Coordinate, Character> cacheKey = Pair.of(position, move);
        Pair<Coordinate, List<Character>> r = movesCache.computeIfAbsent(cacheKey, k -> {
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

            return Pair.of(targetDirectionChar, directionChars);
        });

        positions.set(n - 1, r.first());

        long steps = 0;
        for (Character ch : r.second()) {
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
