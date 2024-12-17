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
    private Dijkstra<CharMatrix> dijkstra;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());
        Coordinate start = Matrix.findChar(map, 'S');
        Coordinate end = Matrix.findChar(map, 'E');

        dijkstra = new Dijkstra<>(
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
    }

    @Override
    protected Object part1(Stream<String> input) {
        return dijkstra.findAllShortestPaths().orElseThrow(); // Your puzzle answer was 83444
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        dijkstra.visualizeAllPaths('O');
        Matrix.print(System.out, dijkstra.costMatrix());
        return dijkstra.getAllPaths().size(); // Your puzzle answer was
        // 445 too low
        // 10135 too high
    }
}
