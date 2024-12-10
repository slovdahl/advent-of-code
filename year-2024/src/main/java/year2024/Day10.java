package year2024;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day10 extends Day {

    private int[][] map;
    private Set<Coordinate> startingPoints;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.intMatrix(input.toList());
        startingPoints = Matrix.findInts(map, 0);
    }

    @Override
    protected Object part1(Stream<String> input) {
        Multimap<Coordinate, Coordinate> topsReached = HashMultimap.create();

        for (Coordinate startingPoint : startingPoints) {
            visit(startingPoint, startingPoint, map, topsReached);
        }

        return topsReached.size(); // Your puzzle answer was 430
    }

    @Override
    protected Object part2(Stream<String> input) {
        Multimap<Coordinate, Coordinate> topsReached = ArrayListMultimap.create();

        for (Coordinate startingPoint : startingPoints) {
            visit(startingPoint, startingPoint, map, topsReached);
        }

        return topsReached.size(); // Your puzzle answer was 928
    }

    private static void visit(Coordinate startingPoint, Coordinate coordinate, int[][] map, Multimap<Coordinate, Coordinate> topsReached) {
        int current = coordinate.at(map);

        if (current == 9) {
            topsReached.put(startingPoint, coordinate);
            return;
        }

        for (Direction direction : Direction.ALL) {
            int directionValue = coordinate.atDirection(map, direction, current);
            if (directionValue == current + 1) {
                visit(startingPoint, coordinate.move(direction), map, topsReached);
            }
        }
    }
}
