package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day12 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        char[][] map = Matrix.matrix(input.toList());
        Set<Coordinate> unvisited = Matrix.toCoordinates(map);

        List<Set<Coordinate>> regions = new ArrayList<>();
        while (!unvisited.isEmpty()) {
            Iterator<Coordinate> iterator = unvisited.iterator();
            Coordinate next = iterator.next();
            iterator.remove();

            char plant = next.at(map);
            Set<Coordinate> region = traverseRegion(plant, map, new HashSet<>(), next);
            unvisited.removeAll(region);
            regions.add(Set.copyOf(region));
        }

        return regions.stream()
                .mapToLong(region -> {
                    long area = region.size();
                    long perimeter = 0;
                    for (Coordinate coordinate : region) {
                        long thisPerimeter = 4;
                        for (Direction direction : Direction.ALL) {
                            if (region.contains(coordinate.move(direction))) {
                                thisPerimeter--;
                            }
                        }
                        perimeter += thisPerimeter;
                    }
                    return area * perimeter;
                })
                .sum(); // Your puzzle answer was
    }

    private static Set<Coordinate> traverseRegion(char plant, char[][] map, Set<Coordinate> region, Coordinate current) {
        if (region.contains(current)) {
            return region;
        }

        region.add(current);

        for (Direction direction : Direction.ALL) {
            if (plant == current.atDirection(map, direction, '.')) {
                traverseRegion(plant, map, region, current.move(direction));
            }
        }

        return region;
    }
}
