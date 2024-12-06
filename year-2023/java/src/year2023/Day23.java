package year2023;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day23 extends Day {

    private static final Coordinate START = new Coordinate(0, 1);

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        char[][] map = Matrix.matrix(input.toList());
        Coordinate end = new Coordinate(map.length - 1, Matrix.findChar(map[map.length - 1], '.'));

        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

        Set<Coordinate> visited = new HashSet<>();

        VisitorTask visitorTask = new VisitorTask(
                map,
                end,
                START,
                Direction.DOWN,
                visited
        );

        return forkJoinPool.invoke(visitorTask) - 1;
    }

    private static class VisitorTask extends RecursiveTask<Integer> {
        private final char[][] map;
        private final Coordinate end;
        private final Coordinate current;
        private final Direction move;
        private final Set<Coordinate> visited;

        VisitorTask(char[][] map,
                    Coordinate end,
                    Coordinate current,
                    Direction move,
                    Set<Coordinate> visited) {

            this.end = end;
            this.current = current;
            this.move = move;
            this.map = map;
            this.visited = visited;
        }

        @Override
        protected Integer compute() {
            visited.add(current);
            if (current.equals(end)) {
                return visited.size();
            }

            char ch = current.at(map, 'X');

            if (ch == 'X') {
                throw new IllegalStateException("Unexpected state");
            }

            if (ch == '>') {
                return new VisitorTask(map, end, current.move(Direction.RIGHT), Direction.RIGHT, visited).compute();
            } else if (ch == '<') {
                return new VisitorTask(map, end, current.move(Direction.LEFT), Direction.LEFT, visited).compute();
            } else if (ch == '^') {
                return new VisitorTask(map, end, current.move(Direction.UP), Direction.UP, visited).compute();
            } else if (ch == 'v') {
                return new VisitorTask(map, end, current.move(Direction.DOWN), Direction.DOWN, visited).compute();
            } else {
                List<VisitorTask> tasks = new ArrayList<>();
                for (Direction direction : Direction.ALL) {
                    if (move.opposite() == direction) {
                        continue;
                    }

                    Coordinate candidate = current.tryMove(map, direction);
                    if (candidate != null && !visited.contains(candidate)) {
                        boolean valid = switch (direction) {
                            case UP -> candidate.at(map) != 'v';
                            case RIGHT -> candidate.at(map) != '<';
                            case DOWN -> candidate.at(map) != '^';
                            case LEFT -> candidate.at(map) != '>';
                        };

                        if (valid) {
                            tasks.add(new VisitorTask(map, end, candidate, direction, new HashSet<>(visited)));
                        }
                    }
                }

                if (tasks.size() == 1) {
                    return tasks.getFirst().compute();
                } else if (tasks.isEmpty()) {
                    return visited.size();
                } else {
                    for (int i = 1; i < tasks.size(); i++) {
                        tasks.get(i).fork();
                    }

                    int longestHikeLength = tasks.getFirst().compute();
                    for (VisitorTask task : tasks) {
                        longestHikeLength = Math.max(longestHikeLength, task.join());
                    }
                    return longestHikeLength;
                }
            }
        }
    }
}
