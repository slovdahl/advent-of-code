package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lib.Coordinate;
import lib.Day;
import lib.Matrix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day8 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        char[][] map = Matrix.matrix(input.toList());

        SetMultimap<Character, Coordinate> antennas = HashMultimap.create();
        Set<Coordinate> antiNodes = new HashSet<>();

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] != '.') {
                    antennas.put(map[row][col], new Coordinate(row, col));
                }
            }
        }

        for (Map.Entry<Character, Collection<Coordinate>> entry : antennas.asMap().entrySet()) {
            Collection<Coordinate> signalAntennas = entry.getValue();

            for (Coordinate antenna1 : signalAntennas) {
                for (Coordinate antenna2 : signalAntennas) {
                    if (antenna1.equals(antenna2)) {
                        continue;
                    }

                    int rowDiff = antenna2.row() - antenna1.row();
                    int colDiff = antenna2.column() - antenna1.column();

                    Coordinate antiNode1 = new Coordinate(antenna1.row() - rowDiff, antenna1.column() - colDiff);
                    if (antiNode1.in(map)) {
                        antiNodes.add(antiNode1);
                    }

                    Coordinate antiNode2 = new Coordinate(antenna2.row() + rowDiff, antenna2.column() + colDiff);
                    if (antiNode2.in(map)) {
                        antiNodes.add(antiNode2);
                    }
                }
            }
        }

        return antiNodes.size(); // Your puzzle answer was 320
    }
}
