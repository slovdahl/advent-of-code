package year2023;

import year2023.tools.Coordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Comparator.comparing;
import static year2023.Common.deepClone;
import static year2023.Common.findChar;
import static year2023.Common.matrix;
import static year2023.Common.print;

@SuppressWarnings("unused")
public class Day21 extends Day {

    private static final boolean DEBUG = false;

    @Override
    Integer part1(Stream<String> input) throws Exception {
        Stream<String> sampleInput = """
                ...........
                .....###.#.
                .###.##..#.
                ..#.#...#..
                ....#.#....
                .##..S####.
                .##..#...#.
                .......##..
                .##.#.####.
                .##..##.##.
                ...........
                """.lines();

        char[][] matrix = matrix(input.toList());

        int stepsToTake = matrix.length == 11 ? 6 : 64;

        Coordinate startingPoint = findChar(matrix, 'S');
        matrix[startingPoint.row()][startingPoint.column()] = '.';

        Set<Coordinate> finalCoordinates = findFinalCoordinates(
                matrix,
                startingPoint,
                stepsToTake,
                new ConcurrentHashMap<>()
        );

        for (Coordinate finalCoordinate : finalCoordinates) {
            matrix[finalCoordinate.row()][finalCoordinate.column()] = 'O';
        }

        print(matrix);
        System.out.println();

        return finalCoordinates.size(); // Your puzzle answer was 3733
    }

    @Override
    Long part2(Stream<String> input) throws Exception {
        char[][] matrix = matrix(input.toList());

        Coordinate startingPoint = findChar(matrix, 'S');
        matrix[startingPoint.row()][startingPoint.column()] = '.';

        int maximumStepsToTake = 26501365;

        Set<Coordinate> allFinalStates = findFinalCoordinates(
                matrix,
                startingPoint,
                matrix.length * 2,
                new ConcurrentHashMap<>()
        );

        Set<Coordinate> allFinalStatesOneStepShifted = findFinalCoordinates(
                matrix,
                startingPoint,
                matrix.length * 2 + 1,
                new ConcurrentHashMap<>()
        );

        int remainingStepsAfterStartingPointMatrix = maximumStepsToTake - (startingPoint.column() + 1) + 1;
        int clonesInEachDirection = remainingStepsAfterStartingPointMatrix / matrix[0].length;

        checkState(remainingStepsAfterStartingPointMatrix % matrix[0].length == 0);
        checkState(clonesInEachDirection * matrix[0].length == remainingStepsAfterStartingPointMatrix);
        checkState(clonesInEachDirection % 2 == 0);

        //
        // LEFT
        //
        Set<Coordinate> finalStatesInLeftMost = new HashSet<>();
        for (Coordinate coordinate : allFinalStatesOneStepShifted) {
            if (coordinate.row() == startingPoint.row() ||
                    (coordinate.row() > startingPoint.row() && coordinate.column() >= coordinate.row() - 65) ||
                    (coordinate.row() < startingPoint.row() && coordinate.column() >= 65 - coordinate.row())) {

                finalStatesInLeftMost.add(coordinate);
            }
        }

        Set<Coordinate> finalStatesInLeftMostAndOneUp = new HashSet<>();
        {
            Coordinate start = finalStatesInLeftMost.stream()
                    .filter(c -> c.row() == 0)
                    .min(comparing(Coordinate::column))
                    .orElseThrow()
                    .withRow(matrix.length - 1);

            for (Coordinate coordinate : allFinalStates) {
                if (coordinate.row() <= start.row() && coordinate.column() >= start.column() + (131 - coordinate.row())) {
                    finalStatesInLeftMostAndOneUp.add(coordinate);
                } else if (coordinate.row() > start.row()) {
                    throw new IllegalStateException();
                }
            }
        }

        Set<Coordinate> finalStatesInLeftMostAndOneUpOneRight = new HashSet<>();
        {
            Coordinate start = finalStatesInLeftMostAndOneUp.stream()
                    .min(comparing(Coordinate::row))
                    .orElseThrow()
                    .withColumn(0);

            for (Coordinate coordinate : allFinalStatesOneStepShifted) {
                if (coordinate.row() >= start.row() ||
                        coordinate.column() >= start.row() - coordinate.row() - 1) {

                    finalStatesInLeftMostAndOneUpOneRight.add(coordinate);
                }
            }
        }

        Set<Coordinate> finalStatesInLeftMostAndOneDown = new HashSet<>();
        {
            Coordinate start = finalStatesInLeftMost.stream()
                    .filter(c -> c.row() == 130)
                    .min(comparing(Coordinate::column))
                    .orElseThrow()
                    .withRow(0);

            for (Coordinate coordinate : allFinalStates) {
                if (coordinate.row() >= start.row() && coordinate.column() > start.column() + coordinate.row()) {
                    finalStatesInLeftMostAndOneDown.add(coordinate);
                } else if (coordinate.row() < start.row()) {
                    throw new IllegalStateException();
                }
            }
        }

        Set<Coordinate> finalStatesInLeftMostAndOneDownOneRight = new HashSet<>();
        {
            Coordinate start = finalStatesInLeftMostAndOneDown.stream()
                    .max(comparing(Coordinate::row))
                    .orElseThrow()
                    .withColumn(0);

            for (Coordinate coordinate : allFinalStatesOneStepShifted) {
                if (coordinate.row() <= start.row() ||
                        coordinate.column() >= coordinate.row() - start.row() - 1) {

                    finalStatesInLeftMostAndOneDownOneRight.add(coordinate);
                }
            }
        }

        //
        // RIGHT
        //
        Set<Coordinate> finalStatesInRightMost = new HashSet<>();
        for (Coordinate coordinate : allFinalStatesOneStepShifted) {
            if (coordinate.row() == startingPoint.row() ||
                    (coordinate.row() > startingPoint.row() && coordinate.column() <= 195 - coordinate.row()) ||
                    (coordinate.row() < startingPoint.row() && coordinate.column() <= 65 + coordinate.row())) {

                finalStatesInRightMost.add(coordinate);
            }
        }

        Set<Coordinate> finalStatesInRightMostAndOneUp = new HashSet<>();
        {
            Coordinate start = finalStatesInRightMost.stream()
                    .filter(c -> c.row() == 0)
                    .max(comparing(Coordinate::column))
                    .orElseThrow()
                    .withRow(matrix.length - 1);

            for (Coordinate coordinate : allFinalStates) {
                if (coordinate.row() <= start.row() && coordinate.column() <= start.column() - (131 - coordinate.row())) {
                    finalStatesInRightMostAndOneUp.add(coordinate);
                } else if (coordinate.row() > start.row()) {
                    throw new IllegalStateException();
                }
            }
        }

        Set<Coordinate> finalStatesInRightMostAndOneUpAndOneLeft = new HashSet<>();
        {
            Coordinate start = finalStatesInRightMostAndOneUp.stream()
                    .min(comparing(Coordinate::row))
                    .orElseThrow()
                    .withColumn(130);

            for (Coordinate coordinate : allFinalStatesOneStepShifted) {
                if (coordinate.row() >= start.row() ||
                        coordinate.column() < start.row() + coordinate.row() - 1) {

                    finalStatesInRightMostAndOneUpAndOneLeft.add(coordinate);
                }
            }
        }

        Set<Coordinate> finalStatesInRightMostAndOneDown = new HashSet<>();
        {
            Coordinate start = finalStatesInRightMost.stream()
                    .filter(c -> c.row() == 130)
                    .max(comparing(Coordinate::column))
                    .orElseThrow()
                    .withRow(0);

            for (Coordinate coordinate : allFinalStates) {
                if (coordinate.row() >= start.row() && coordinate.column() < start.column() - coordinate.row()) {
                    finalStatesInRightMostAndOneDown.add(coordinate);
                } else if (coordinate.row() < start.row()) {
                    throw new IllegalStateException();
                }
            }
        }

        Set<Coordinate> finalStatesInRightMostAndOneDownAndOneLeft = new HashSet<>();
        {
            Coordinate start = finalStatesInRightMostAndOneDown.stream()
                    .max(comparing(Coordinate::row))
                    .orElseThrow()
                    .withColumn(130);

            for (Coordinate coordinate : allFinalStatesOneStepShifted) {
                if (coordinate.row() <= start.row() ||
                        coordinate.column() <= 131 - (coordinate.row() - start.row())) {

                    finalStatesInRightMostAndOneDownAndOneLeft.add(coordinate);
                }
            }
        }

        //
        // TOP
        //
        Set<Coordinate> finalStatesInTopMost = new HashSet<>();
        for (Coordinate coordinate : allFinalStatesOneStepShifted) {
            if (coordinate.column() == startingPoint.column() ||
                    (coordinate.column() > startingPoint.column() && coordinate.row() >= coordinate.column() - 65) ||
                    (coordinate.column() < startingPoint.column() && coordinate.row() >= 65 - coordinate.column())) {

                finalStatesInTopMost.add(coordinate);
            }
        }

        //
        // BOTTOM
        //
        Set<Coordinate> finalStatesInBottomMost = new HashSet<>();
        for (Coordinate coordinate : allFinalStatesOneStepShifted) {
            if (coordinate.column() == startingPoint.column() ||
                    (coordinate.column() > startingPoint.column() && coordinate.row() <= 195 - coordinate.column()) ||
                    (coordinate.column() < startingPoint.column() && coordinate.row() <= 65 + coordinate.column())) {

                finalStatesInBottomMost.add(coordinate);
            }
        }

        if (DEBUG) {
            // leftmost-2
            printWithFinalStates(matrix, Set.of(), finalStatesInLeftMostAndOneUp, finalStatesInLeftMostAndOneUpOneRight);

            // leftmost-1
            printWithFinalStates(matrix, finalStatesInLeftMostAndOneUp, finalStatesInLeftMostAndOneUpOneRight);

            // leftmost
            printWithFinalStates(matrix, finalStatesInLeftMost, allFinalStatesOneStepShifted);

            // leftmost+1
            printWithFinalStates(matrix, finalStatesInLeftMostAndOneDown, finalStatesInLeftMostAndOneDownOneRight);

            System.out.println();
            System.out.println();

            // rightmost-1
            printWithFinalStates(matrix, finalStatesInRightMostAndOneUpAndOneLeft, finalStatesInRightMostAndOneUp);

            // rightmost
            printWithFinalStates(matrix, allFinalStatesOneStepShifted, finalStatesInRightMost);

            // rightmost+1
            printWithFinalStates(matrix, finalStatesInRightMostAndOneDownAndOneLeft, finalStatesInRightMostAndOneDown);

            System.out.println();
            System.out.println();

            // topmost
            printWithFinalStates(matrix, finalStatesInLeftMostAndOneUp, finalStatesInTopMost, finalStatesInRightMostAndOneUp);

            System.out.println();
            System.out.println();

            // bottommost-1
            printWithFinalStates(matrix, finalStatesInLeftMostAndOneDown, finalStatesInLeftMostAndOneDownOneRight, allFinalStatesOneStepShifted, finalStatesInRightMostAndOneDownAndOneLeft, finalStatesInRightMostAndOneDown);

            // bottommost
            printWithFinalStates(matrix, Set.of(), finalStatesInLeftMostAndOneDown, finalStatesInBottomMost, finalStatesInRightMostAndOneDown, Set.of());
        }

        int bottomRow = (2 * clonesInEachDirection) + 1;

        return IntStream.rangeClosed(1, bottomRow)
                .parallel()
                .mapToLong(row -> {
                    if (row == 1) {
                        // Top row
                        return (long) finalStatesInLeftMostAndOneUp.size() +
                                finalStatesInTopMost.size() +
                                finalStatesInRightMostAndOneUp.size();
                    } else if (row == bottomRow) {
                        // Bottom row
                        return (long) finalStatesInLeftMostAndOneDown.size() +
                                finalStatesInBottomMost.size() +
                                finalStatesInRightMostAndOneDown.size();
                    } else if (row == clonesInEachDirection + 1) {
                        // Middle row
                        return (row - 1L) * allFinalStates.size() +
                                (row - 2L) * allFinalStatesOneStepShifted.size() +
                                finalStatesInLeftMost.size() +
                                finalStatesInRightMost.size() +
                                1 /* starting point */;
                    } else if (row <= clonesInEachDirection) {
                        // Top half
                        return finalStatesInLeftMostAndOneUp.size() +
                                finalStatesInLeftMostAndOneUpOneRight.size() +
                                (row - 1L) * allFinalStates.size() +
                                (row - 2L) * allFinalStatesOneStepShifted.size() +
                                finalStatesInRightMostAndOneUp.size() +
                                finalStatesInRightMostAndOneUpAndOneLeft.size();
                    } else if (row > clonesInEachDirection + 1) {
                        // Bottom half
                        return finalStatesInLeftMostAndOneDown.size() +
                                finalStatesInLeftMostAndOneDownOneRight.size() +
                                (long) (bottomRow - row) * allFinalStates.size() +
                                (long) (bottomRow - row - 1) * allFinalStatesOneStepShifted.size() +
                                finalStatesInRightMostAndOneDown.size() +
                                finalStatesInRightMostAndOneDownAndOneLeft.size();
                    } else {
                        throw new IllegalStateException();
                    }
                })
                .sum();

        // 617720199619352 too low
        // 617723253120458 too low
        // 617723253128027 too low
        // 617726306629133 incorrect
        // 617726306644227 incorrect
        // 617723253143121 incorrect
        // 617729360145333 incorrect
        // 617729392918089 incorrect
        // 617729388062901 incorrect(?)

        // 617729388062902
    }

    private static Set<Coordinate> findFinalCoordinates(char[][] matrix, Coordinate startingPoint, int stepsToTake, ConcurrentMap<Coordinate, Integer> visitedCoordinates) {
        Set<Coordinate> finalCoordinates = ConcurrentHashMap.newKeySet();

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        VisitorTask visitorTask = new VisitorTask(
                new CoordinateState(startingPoint, 0),
                matrix,
                visitedCoordinates,
                finalCoordinates,
                stepsToTake
        );

        Void result = forkJoinPool.invoke(visitorTask);

        return finalCoordinates;
    }

    @SafeVarargs
    private static void printWithFinalStates(char[][] matrix, Set<Coordinate>... states) {
        List<char[][]> matrices = new ArrayList<>(states.length);
        for (Set<Coordinate> state : states) {
            char[][] m = deepClone(matrix);
            for (Coordinate coordinate : state) {
                m[coordinate.row()][coordinate.column()] = 'O';
            }
            matrices.add(m);
        }
        print(matrices.toArray(new char[0][0][0]));
    }

    private record CoordinateState(Coordinate coordinate, int steps) {
        CoordinateState move(Coordinate coordinate) {
            return new CoordinateState(coordinate, steps + 1);
        }
    }

    private static class VisitorTask extends RecursiveAction {

        private final CoordinateState current;
        private final char[][] matrix;
        private final ConcurrentMap<Coordinate, Integer> visitedCoordinates;
        private final Set<Coordinate> finalCoordinates;
        private final int stepsToTake;
        private final int reminder;

        VisitorTask(CoordinateState current,
                    char[][] matrix,
                    ConcurrentMap<Coordinate, Integer> visitedCoordinates,
                    Set<Coordinate> finalCoordinates,
                    int stepsToTake) {

            this.current = current;
            this.matrix = matrix;
            this.visitedCoordinates = visitedCoordinates;
            this.finalCoordinates = finalCoordinates;
            this.stepsToTake = stepsToTake;
            reminder = stepsToTake % 2;
        }

        @Override
        protected void compute() {
            Integer previousSteps = visitedCoordinates.get(current.coordinate());
            Integer newSteps = visitedCoordinates.compute(current.coordinate(), (coordinate, steps) -> {
                if (steps == null || steps > current.steps()) {
                    return current.steps();
                } else {
                    return steps;
                }
            });

            if (current.steps() % 2 == reminder) {
                finalCoordinates.add(current.coordinate());
            }

            if (current.steps() == stepsToTake || Objects.equals(previousSteps, newSteps)) {
                return;
            }

            List<VisitorTask> tasks = new ArrayList<>();

            if (current.coordinate().upOr(matrix, '#') == '.') {
                Coordinate next = current.coordinate().moveUp();
                if (!isVisitedWithFewerStepsTaken(next, current.steps())) {
                    tasks.add(new VisitorTask(current.move(next), matrix, visitedCoordinates, finalCoordinates, stepsToTake));
                }
            }
            if (current.coordinate().rightOr(matrix, '#') == '.') {
                Coordinate next = current.coordinate().moveRight();
                if (!isVisitedWithFewerStepsTaken(next, current.steps())) {
                    tasks.add(new VisitorTask(current.move(next), matrix, visitedCoordinates, finalCoordinates, stepsToTake));
                }
            }
            if (current.coordinate().downOr(matrix, '#') == '.') {
                Coordinate next = current.coordinate().moveDown();
                if (!isVisitedWithFewerStepsTaken(next, current.steps())) {
                    tasks.add(new VisitorTask(current.move(next), matrix, visitedCoordinates, finalCoordinates, stepsToTake));
                }
            }
            if (current.coordinate().leftOr(matrix, '#') == '.') {
                Coordinate next = current.coordinate().moveLeft();
                if (!isVisitedWithFewerStepsTaken(next, current.steps())) {
                    tasks.add(new VisitorTask(current.move(next), matrix, visitedCoordinates, finalCoordinates, stepsToTake));
                }
            }

            if (!tasks.isEmpty()) {
                invokeAll(tasks);
            }
        }

        private boolean isVisitedWithFewerStepsTaken(Coordinate coordinate, int currentSteps) {
            Integer steps = visitedCoordinates.get(coordinate);
            return steps != null && currentSteps + 1 > steps;
        }
    }
}
