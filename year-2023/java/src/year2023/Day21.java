package year2023;

import year2023.tools.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Stream;

import static year2023.Common.findChar;
import static year2023.Common.matrix;
import static year2023.Common.print;

@SuppressWarnings("unused")
public class Day21 extends Day {

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
        assert stepsToTake % 2 == 0;

        Coordinate startingPoint = findChar(matrix, 'S');
        matrix[startingPoint.row()][startingPoint.column()] = '.';

        ConcurrentMap<Coordinate, Integer> visitedCoordinates = new ConcurrentHashMap<>();
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

        for (Coordinate finalCoordinate : finalCoordinates) {
            matrix[finalCoordinate.row()][finalCoordinate.column()] = 'O';
        }

        print(matrix);

        return finalCoordinates.size(); // Your puzzle answer was 3733
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

            if (current.steps() % 2 == 0) {
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
