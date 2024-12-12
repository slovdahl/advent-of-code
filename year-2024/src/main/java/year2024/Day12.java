package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lib.Coordinate;
import lib.Day;
import lib.Direction;
import lib.Matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

@SuppressWarnings("unused")
public class Day12 extends Day {

    private List<Region> regions;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        char[][] map = Matrix.matrix(input.toList());
        Set<Coordinate> unvisited = Matrix.toCoordinates(map);

        regions = new ArrayList<>();
        while (!unvisited.isEmpty()) {
            Iterator<Coordinate> iterator = unvisited.iterator();
            Coordinate next = iterator.next();
            iterator.remove();

            char plant = next.at(map);
            Set<Coordinate> region = traverseRegion(plant, map, new HashSet<>(), next);
            unvisited.removeAll(region);
            regions.add(new Region(plant, Set.copyOf(region)));
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        return regions.stream()
                .mapToLong(region -> {
                    long area = region.coordinates().size();
                    long perimeter = 0;
                    for (Coordinate coordinate : region.coordinates()) {
                        long thisPerimeter = 4;
                        for (Direction direction : Direction.ALL) {
                            if (region.coordinates().contains(coordinate.move(direction))) {
                                thisPerimeter--;
                            }
                        }
                        perimeter += thisPerimeter;
                    }
                    return area * perimeter;
                })
                .sum(); // Your puzzle answer was 1488414
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return regions.stream()
                .mapToLong(region -> {
                    long area = region.coordinates().size();

                    long sides = 0;
                    Multimap<Direction, Coordinate> perimeter = HashMultimap.create();
                    for (Coordinate coordinate : region.coordinates()) {
                        long thisPerimeter = 4;
                        for (Direction direction : Direction.ALL) {
                            if (region.coordinates().contains(coordinate.move(direction))) {
                                thisPerimeter--;
                                continue;
                            }

                            perimeter.put(direction, coordinate);
                        }

                        sides += thisPerimeter;
                    }

                    for (Map.Entry<Direction, Collection<Coordinate>> entry : perimeter.asMap().entrySet()) {
                        Direction direction = entry.getKey();
                        if (direction == Direction.UP || direction == Direction.DOWN) {
                            Map<Integer, List<Coordinate>> columnsByRow = entry.getValue().stream()
                                    .sorted(comparing(Coordinate::column))
                                    .collect(groupingBy(Coordinate::row));

                            for (Map.Entry<Integer, List<Coordinate>> e2 : columnsByRow.entrySet()) {
                                Integer row = e2.getKey();
                                List<Coordinate> columns = e2.getValue();
                                for (int i = 0; i < columns.size(); i++) {
                                    Coordinate current = columns.get(i);
                                    if (i < columns.size() - 1) {
                                        Coordinate next = columns.get(i + 1);
                                        if (current.column() + 1 == next.column()) {
                                            sides--;
                                        }
                                    }
                                }
                            }
                        } else {
                            Map<Integer, List<Coordinate>> rowsByColumn = entry.getValue().stream()
                                    .sorted(comparing(Coordinate::row))
                                    .collect(groupingBy(Coordinate::column));

                            for (Map.Entry<Integer, List<Coordinate>> e2 : rowsByColumn.entrySet()) {
                                Integer column = e2.getKey();
                                List<Coordinate> rows = e2.getValue();

                                for (int i = 0; i < rows.size(); i++) {
                                    Coordinate current = rows.get(i);
                                    if (i < rows.size() - 1) {
                                        Coordinate next = rows.get(i + 1);
                                        if (current.row() + 1 == next.row()) {
                                            sides--;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return area * sides;
                })
                .sum(); // Your puzzle answer was 911750
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

    private record Region(char plant, Set<Coordinate> coordinates) {
    }
}
