package year2025;

import lib.Coordinate;
import lib.Day;
import lib.Matrix;
import lib.Parse;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

@SuppressWarnings("unused")
public class Day9 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Coordinate> redTiles = input
                .map(line -> {
                    List<Integer> coordinates = Parse.commaSeparatedInts(line);
                    return new Coordinate(coordinates.getFirst(), coordinates.getLast());
                })
                .toList();

        int maxRow = redTiles.stream()
                .max(comparing(Coordinate::row))
                .map(Coordinate::row)
                .orElseThrow();

        int maxColumn = redTiles.stream()
                .max(comparing(Coordinate::column))
                .map(Coordinate::column)
                .orElseThrow();

        SortedSet<Long> areas = new TreeSet<>();
        for (Coordinate coordinate : redTiles) {
            areas.addAll(
                    redTiles.stream()
                            .filter(other -> !coordinate.equals(other))
                            .map(other -> Matrix.area(other, coordinate))
                            .toList()
            );
        }

        return areas.getLast(); // Your puzzle answer was 4781235324.
    }
}
