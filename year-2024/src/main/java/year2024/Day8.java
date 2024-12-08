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

    private char[][] map;
    private SetMultimap<Character, Coordinate> antennas;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());

        antennas = HashMultimap.create();

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col] != '.') {
                    antennas.put(map[row][col], new Coordinate(row, col));
                }
            }
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        Set<Coordinate> antiNodes = new HashSet<>();

        for (Map.Entry<Character, Collection<Coordinate>> entry : antennas.asMap().entrySet()) {
            Collection<Coordinate> signalAntennas = entry.getValue();

            for (Coordinate antenna1 : signalAntennas) {
                for (Coordinate antenna2 : signalAntennas) {
                    if (antenna1.equals(antenna2)) {
                        continue;
                    }

                    int rowDiff = antenna2.row() - antenna1.row();
                    int colDiff = antenna2.column() - antenna1.column();

                    Coordinate antiNode1 = antenna1.tryMove(map, -rowDiff, -colDiff);
                    if (antiNode1 != null) {
                        antiNodes.add(antiNode1);
                    }

                    Coordinate antiNode2 = antenna2.tryMove(map, rowDiff, colDiff);
                    if (antiNode2 != null) {
                        antiNodes.add(antiNode2);
                    }
                }
            }
        }

        return antiNodes.size(); // Your puzzle answer was 320
    }

    @Override
    protected Object part2(Stream<String> input) {
        Set<Coordinate> antiNodes = new HashSet<>();

        for (Map.Entry<Character, Collection<Coordinate>> entry : antennas.asMap().entrySet()) {
            Collection<Coordinate> signalAntennas = entry.getValue();

            for (Coordinate antenna1 : signalAntennas) {
                for (Coordinate antenna2 : signalAntennas) {
                    if (antenna1.equals(antenna2)) {
                        continue;
                    }

                    antiNodes.add(antenna1);
                    antiNodes.add(antenna2);

                    int rowDiff = antenna2.row() - antenna1.row();
                    int colDiff = antenna2.column() - antenna1.column();

                    for (int i = 1; i < Integer.MAX_VALUE; i++) {
                        Coordinate antiNode1 = antenna1.tryMove(map, -(rowDiff * i), -(colDiff * i));
                        Coordinate antiNode2 = antenna2.tryMove(map, rowDiff * i, colDiff * i);

                        if (antiNode1 != null) {
                            antiNodes.add(antiNode1);
                        }
                        if (antiNode2 != null) {
                            antiNodes.add(antiNode2);
                        }

                        if (antiNode1 == null && antiNode2 == null) {
                            break;
                        }
                    }
                }
            }
        }

        return antiNodes.size(); // Your puzzle answer was 1157
    }
}
