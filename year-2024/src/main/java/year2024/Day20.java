package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day20 extends Day {

    private char[][] map;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());
    }

    @Override
    protected Object part1(Stream<String> input) {
        Coordinate start = Matrix.findChar(map, 'S');
        Coordinate end = Matrix.findChar(map, 'E');

        Dijkstra<CharMatrix> dijkstra = new Dijkstra<>(
                new CharMatrix(map, coordinate -> coordinate.at(map) != '#'),
                start,
                end
        );

        int normalPicoseconds = dijkstra.traverse().orElseThrow();
        List<Coordinate> raceTrack = dijkstra.getLowestCostPath();

        List<Coordinate> cheats = new ArrayList<>();
        for (Coordinate c : raceTrack) {
            for (Direction direction : Direction.ALL) {
                Coordinate cheatCandidate = c.tryMove(map, direction);
                if (cheatCandidate == null || cheatCandidate.at(map) != '#') {
                    continue;
                }

                Coordinate nextAfterCheatCandidate = cheatCandidate.tryMove(map, direction);
                if (nextAfterCheatCandidate != null && nextAfterCheatCandidate.at(map) == '.') {
                    cheats.add(cheatCandidate);
                }
            }
        }

        Map<Coordinate, Integer> savedTimePerCheat = new HashMap<>();
        for (Coordinate cheat : cheats) {
            char[][] cheatMap = Matrix.deepClone(this.map);
            cheat.set(cheatMap, '.');

            Dijkstra<CharMatrix> cheatDijkstra = new Dijkstra<>(
                    new CharMatrix(cheatMap, coordinate -> coordinate.at(cheatMap) != '#'),
                    start,
                    end
            );

            int cheatPicoseconds = cheatDijkstra.traverse().orElseThrow();

            if (cheatPicoseconds < normalPicoseconds) {
                savedTimePerCheat.put(cheat, normalPicoseconds - cheatPicoseconds);
            }
        }

        return savedTimePerCheat.values().stream()
                .filter(savedTime -> savedTime >= 100)
                .count(); // Your puzzle answer was 1395
    }
}
