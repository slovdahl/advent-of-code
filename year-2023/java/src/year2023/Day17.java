package year2023;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;

import java.util.stream.Stream;

import static lib.Matrix.intMatrix;

@SuppressWarnings("unused")
public class Day17 extends Day {

    @Override
    protected Mode mode() {
        return Mode.SAMPLE_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        int[][] matrix = intMatrix(input.toList());

        Coordinate start = new Coordinate(0, 0);
        Coordinate end = new Coordinate(matrix.length - 1, matrix[0].length - 1);

        Dijkstra dijkstra = new Dijkstra(
                matrix,
                start,
                end,
                (m, from, to) -> to.at(m)
        );

        int result = dijkstra.traverse();
        //dijkstra.visualize();
        System.out.println();
        dijkstra.visualizeCosts();
        return result;
        // 688 incorrect
    }
    // 1255 too high
    // 1253 too high
}
