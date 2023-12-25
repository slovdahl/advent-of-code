package year2023;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;
import year2023.tools.Coordinate;
import year2023.tools.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@SuppressWarnings({"unused"})
public class Day18 extends Day {

    @Override
    Long part1(Stream<String> input) throws IOException {
        SparseMatrix matrix = new SparseMatrix();
        Coordinate current = new Coordinate(0, 0);

        for (String line : input.toList()) {
            String[] split = line.split(" ");

            Direction direction = Coordinate.from(split[0]);
            int steps = Integer.parseInt(split[1]);

            current = matrix.add(
                    current,
                    direction,
                    steps
            );
        }

        mergeAdjacentRanges(matrix);

        return calculateCubicMetersForLava(matrix); // Your puzzle answer was 42317
    }

    private static void mergeAdjacentRanges(SparseMatrix matrix) {
        for (SortedSet<ContiguousSet<Integer>> row : matrix.matrix) {
            if (row.isEmpty()) {
                continue;
            }

            boolean changed;
            do {
                changed = false;
                Iterator<ContiguousSet<Integer>> iterator = row.iterator();

                ContiguousSet<Integer> current = iterator.next();

                while (iterator.hasNext()) {
                    ContiguousSet<Integer> next = iterator.next();

                    if (!current.intersection(next).isEmpty() || current.last() + 1 == next.first()) {
                        row.remove(current);
                        row.remove(next);

                        row.add(ContiguousSet.create(
                                current.range().span(next.range()),
                                DiscreteDomain.integers()
                        ));

                        changed = true;
                        break;
                    }

                    current = next;
                }
            } while (changed);
        }
    }

    private static long calculateCubicMetersForLava(SparseMatrix matrix) {
        long enclosedTiles = 0L;
        Map<Integer, AtomicLong> enclosedTilesPerRow = new LinkedHashMap<>();

        for (int row = 0; row < matrix.rows(); row++) {
            SortedSet<ContiguousSet<Integer>> rowContent = matrix.row(row);

            SortedSet<ContiguousSet<Integer>> previousRowRanges = null;
            if (row > 0) {
                previousRowRanges = matrix.row(row - 1);
            }

            SortedSet<ContiguousSet<Integer>> nextRowRanges = null;
            if (row < matrix.rows() - 1) {
                nextRowRanges = matrix.row(row + 1);
            }

            int seenEdges = 0;

            ContiguousSet<Integer> previous = null;
            for (ContiguousSet<Integer> current : rowContent) {
                enclosedTiles += current.size();
                enclosedTilesPerRow.computeIfAbsent(row, r -> new AtomicLong())
                        .addAndGet(current.size());

                if (previous != null && seenEdges % 2 == 1) {
                    int toAdd = Math.abs(previous.last() - current.first() - 1) - 2;

                    enclosedTiles += toAdd;
                    enclosedTilesPerRow.computeIfAbsent(row, r -> new AtomicLong())
                            .addAndGet(toAdd);
                }

                if (current.size() == 1) {
                    seenEdges++;
                } else {
                    boolean startUp = false;
                    boolean startDown = false;
                    boolean endUp = false;
                    boolean endDown = false;

                    if (previousRowRanges != null) {
                        for (ContiguousSet<Integer> previousRowRange : previousRowRanges) {
                            if (previousRowRange.contains(current.first())) {
                                startUp = true;
                            } else if (previousRowRange.contains(current.last())) {
                                endUp = true;
                            }
                        }
                    }

                    if (nextRowRanges != null) {
                        for (ContiguousSet<Integer> nextRowRange : nextRowRanges) {
                            if (nextRowRange.contains(current.first())) {
                                startDown = true;
                            } else if (nextRowRange.contains(current.last())) {
                                endDown = true;
                            }
                        }
                    }

                    if (startUp && startDown || endUp && endDown || !startUp && !startDown || !endUp && !endDown) {
                        throw new IllegalStateException();
                    } else if (startUp && endUp || startDown && endDown) {
                        seenEdges += 2;
                    } else {
                        seenEdges++;
                    }
                }

                previous = current;
            }

            if (seenEdges % 2 == 1) {
                throw new IllegalStateException("row " + row);
            }
        }

        return enclosedTiles;
    }

    private static class SparseMatrix {

        private List<SortedSet<ContiguousSet<Integer>>> matrix;
        private int count;

        SparseMatrix() {
            matrix = new ArrayList<>();
            matrix.add(newRow());
        }

        Coordinate add(Coordinate startingPoint, Direction direction, int steps) {
            if (direction == Direction.UP) {
                if (startingPoint.row() - steps < 0) {
                    int rowsToAdd = Math.abs(startingPoint.row() - steps);

                    prependNumberOfRows(rowsToAdd);

                    startingPoint = new Coordinate(startingPoint.row() + rowsToAdd, startingPoint.column());
                }

                Range<Integer> range = Range.singleton(startingPoint.column());
                for (int step = 1; step <= steps; step++) {
                    addToRow(startingPoint.row() - step, range);
                }

                return new Coordinate(startingPoint.row() - steps, startingPoint.column());
            } else if (direction == Direction.RIGHT) {
                int end = startingPoint.column() + steps;

                addToRow(startingPoint.row(), Range.closed(startingPoint.column(), end));

                return new Coordinate(startingPoint.row(), end);
            } else if (direction == Direction.DOWN) {
                if (startingPoint.row() + steps >= matrix.size()) {
                    int rowsToAdd = startingPoint.row() + steps - matrix.size();
                    for (int i = 0; i <= rowsToAdd; i++) {
                        matrix.add(newRow());
                    }
                }

                Range<Integer> range = Range.singleton(startingPoint.column());
                for (int step = 1; step <= steps; step++) {
                    addToRow(startingPoint.row() + step, range);
                }

                return new Coordinate(startingPoint.row() + steps, startingPoint.column());
            } else if (direction == Direction.LEFT) {
                int end = startingPoint.column() - steps;

                addToRow(startingPoint.row(), Range.closed(end, startingPoint.column()));

                return new Coordinate(startingPoint.row(), startingPoint.column() - steps);
            }

            throw new IllegalStateException();
        }

        private void prependNumberOfRows(int rowsToAdd) {
            List<SortedSet<ContiguousSet<Integer>>> newMatrix = new ArrayList<>(matrix.size() + rowsToAdd);
            for (int i = 0; i < rowsToAdd; i++) {
                newMatrix.add(newRow());
            }
            newMatrix.addAll(matrix);

            matrix = newMatrix;
        }

        private SortedSet<ContiguousSet<Integer>> newRow() {
            return new TreeSet<>(
                    Comparator.<ContiguousSet<Integer>, Integer>comparing(ContiguousSet::first)
                            .thenComparing(ContiguousSet::last)
            );
        }

        void addToRow(int row, Range<Integer> range) {
            getOrCreate(row)
                    .add(ContiguousSet.create(range, DiscreteDomain.integers()));
        }

        SortedSet<ContiguousSet<Integer>> getOrCreate(int row) {
            SortedSet<ContiguousSet<Integer>> rowContent = matrix.get(row);

            if (rowContent == null) {
                rowContent = newRow();
                matrix.set(row, rowContent);
            }

            return rowContent;
        }

        int rows() {
            return matrix.size();
        }

        SortedSet<ContiguousSet<Integer>> row(int row) {
            return matrix.get(row);
        }

        Stream<SortedSet<ContiguousSet<Integer>>> stream() {
            return matrix.stream();
        }
    }
}
