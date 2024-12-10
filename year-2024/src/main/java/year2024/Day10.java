package year2024;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lib.Coordinate;
import lib.Day;
import lib.Matrix;

import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day10 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        int[][] map = Matrix.intMatrix(input.toList());

        Set<Coordinate> startingPoints = Matrix.findInts(map, 0);
        Multimap<Coordinate, Coordinate> topsReached = HashMultimap.create();

        for (Coordinate startingPoint : startingPoints) {
            visit(startingPoint, startingPoint, map, topsReached);
        }

        return topsReached.size(); // Your puzzle answer was 430
    }

    private static void visit(Coordinate startingPoint, Coordinate coordinate, int[][] map, Multimap<Coordinate, Coordinate> topsReached) {
        int current = coordinate.at(map);

        if (current == 9) {
            topsReached.put(startingPoint, coordinate);
            return;
        }

        int up = coordinate.upOr(map, current);
        if (up == current + 1) {
            visit(startingPoint, coordinate.moveUp(), map, topsReached);
        }

        int right = coordinate.rightOr(map, current);
        if (right == current + 1) {
            visit(startingPoint, coordinate.moveRight(), map, topsReached);
        }

        int down = coordinate.downOr(map, current);
        if (down == current + 1) {
            visit(startingPoint, coordinate.moveDown(), map, topsReached);
        }

        int left = coordinate.leftOr(map, current);
        if (left == current + 1) {
            visit(startingPoint, coordinate.moveLeft(), map, topsReached);
        }
    }
}
