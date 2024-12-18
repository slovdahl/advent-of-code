package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Matrix;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day18 extends Day {

    private List<Coordinate> bytePositions;

    private char[][] map;
    private Coordinate start;
    private Coordinate end;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        bytePositions = input
                .map(s -> {
                    String[] split = s.split(",");
                    return new Coordinate(split[1], split[0]);
                })
                .toList();

        int rowsAndColumns = mode() == Mode.REAL_INPUT ? 71 : 7;
        map = Matrix.matrix(rowsAndColumns, rowsAndColumns, '.');

        start = new Coordinate(0, 0);
        end = new Coordinate(map.length - 1, map[0].length - 1);
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Coordinate> limitedPositions = bytePositions.stream()
                .limit(mode() == Mode.REAL_INPUT ? 1024 : 12)
                .toList();

        char[][] map = Matrix.deepClone(this.map);

        for (Coordinate bytePosition : limitedPositions) {
            bytePosition.set(map, '#');
        }

        Dijkstra<CharMatrix> dijkstra = new Dijkstra<>(
                new CharMatrix(map, coordinate -> coordinate.at(map) != '#'),
                start,
                end,
                (charMatrix, direction, current, next) -> 1
        );

        return dijkstra.traverse(); // Your puzzle answer was 338
    }
}
