package year2025;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day7 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        char[][] manifold = Matrix.matrix(input.toList());

        Coordinate start = new Coordinate(0, Matrix.findChar(manifold[0], 'S'));

        int splits = 0;

        Set<Coordinate> next = new HashSet<>();
        next.add(start.moveDown());
        for (int i = 1; i < manifold.length; i++) {
            Set<Coordinate> current = new HashSet<>(next);
            next.clear();
            for (Coordinate c : current) {
                if (c.at(manifold) == '.') {
                    c.set(manifold, '|');
                    Coordinate down = c.tryMove(manifold, Direction.DOWN);
                    if (down != null) {
                        next.add(down);
                    }

                    continue;
                }

                if (c.at(manifold) == '^') {
                    splits++;

                    Coordinate left = c.tryMove(manifold, Direction.LEFT);
                    if (left != null) {
                        left.set(manifold, '|');
                        Coordinate down = left.tryMove(manifold, Direction.DOWN);
                        if (down != null) {
                            next.add(down);
                        }
                    }

                    Coordinate right = c.tryMove(manifold, Direction.RIGHT);
                    if (right != null) {
                        right.set(manifold, '|');
                        Coordinate down = right.tryMove(manifold, Direction.DOWN);
                        if (down != null) {
                            next.add(down);
                        }
                    }
                }
            }
        }

        return splits; // Your puzzle answer was 1541.
    }
}
