package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day16 extends Day {

    private char[][] map;
    private Coordinate start;
    private Coordinate end;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());
        start = Matrix.findChar(map, 'S');
        end = Matrix.findChar(map, 'E');
    }

    @Override
    protected Object part1(Stream<String> input) {
        Dijkstra<CharMatrix> dijkstra = new Dijkstra<>(
                new CharMatrix(map, coordinate -> coordinate.at(map) != '#'),
                start,
                end,
                (charMatrix, direction, current, next) -> {
                    if (direction == null) {
                        direction = Direction.RIGHT;
                    }

                    return 1 + (direction == current.directionTo(next) ? 0 : 1_000);
                }
        );

        return dijkstra.findShortestPath().orElseThrow(); // Your puzzle answer was 83444
    }
}
