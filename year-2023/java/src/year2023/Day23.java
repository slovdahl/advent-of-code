package year2023;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.ArrayList;
import java.util.Collection;
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

        // TODO: does not work, mutable instances modified concurrently
        List<Set<Coordinate>> visited = new ArrayList<>();
        visited.add(new HashSet<>());

        VisitorTask visitorTask = new VisitorTask(
                map,
                end,
                START,
                Direction.DOWN,
                visited
        );

        return forkJoinPool.invoke(visitorTask) - 1; // 2180 too low for part 2
    }

    private static class VisitorTask extends RecursiveTask<Integer> {
        private final char[][] map;
        private final Coordinate end;
        private final Coordinate current;
        private final Direction move;
        private final List<Set<Coordinate>> visited;
        private final int depth;

        VisitorTask(char[][] map, Coordinate end, Coordinate current, Direction move, List<Set<Coordinate>> visited) {
            this(map, end, current, move, visited, 0);
        }

        VisitorTask(char[][] map, Coordinate end, Coordinate current, Direction move, List<Set<Coordinate>> visited, int depth) {
            this.map = map;
            this.end = end;
            this.current = current;
            this.move = move;
            this.visited = visited;
            this.depth = depth;
        }

        @Override
        protected Integer compute() {
            visited.getLast().add(current);
            if (current.equals(end)) {
                return visited.stream()
                        .mapToInt(Set::size)
                        .sum();
            }

            char ch = current.at(map, 'X');

            if (ch == 'X') {
                throw new IllegalStateException("Unexpected state");
            }

            List<CandidateCoordinate> candidates = new ArrayList<>();
            for (Direction direction : Direction.ALL) {
                if (move.opposite() == direction) {
                    continue;
                }

                Coordinate candidate = current.tryMove(map, direction);
                if (candidate != null && candidate.at(map) != '#') {
                    boolean alreadyVisited = false;
                    for (Set<Coordinate> coordinates : visited) {
                        alreadyVisited |= coordinates.contains(candidate);
                    }

                    if (!alreadyVisited) {
                        candidates.add(new CandidateCoordinate(candidate, direction));
                    }
                }
            }

            if (candidates.size() == 1) {
                CandidateCoordinate candidate = candidates.getFirst();
                return new VisitorTask(map, end, candidate.coordinate(), candidate.direction(), visited, depth).compute();
            } else if (candidates.isEmpty()) {
                return visited.stream()
                        .mapToInt(Set::size)
                        .sum();
            } else {
                if (depth < ForkJoinPool.getCommonPoolParallelism()) {
                    List<VisitorTask> tasks = new ArrayList<>();
                    for (int i = 1; i < candidates.size(); i++) {
                        CandidateCoordinate candidate = candidates.get(i);
                        List<Set<Coordinate>> newVisited = new ArrayList<>();
                        for (Set<Coordinate> coordinates : visited) {
                            newVisited.add(Set.copyOf(coordinates));
                        }
                        newVisited.add(new HashSet<>());
                        VisitorTask task = new VisitorTask(map, end, candidate.coordinate(), candidate.direction(), newVisited, depth + 1);
                        tasks.add(task);
                        task.fork();
                    }

                    CandidateCoordinate firstCandidate = candidates.getFirst();
                    int longestHikeLength = new VisitorTask(map, end, firstCandidate.coordinate(), firstCandidate.direction(), visited, depth).compute();
                    for (VisitorTask task : tasks) {
                        longestHikeLength = Math.max(longestHikeLength, task.join());
                    }
                    return longestHikeLength;
                } else {
                    int longestHikeLength = 0;
                    for (CandidateCoordinate candidate : candidates) {
                        VisitorTask task = new VisitorTask(map, end, candidate.coordinate(), candidate.direction(), visited, depth);
                        longestHikeLength = Math.max(longestHikeLength, task.compute());
                    }

                    return longestHikeLength;
                }
            }
        }

        private record CandidateCoordinate(Coordinate coordinate, Direction direction) {
        }
    }
}
