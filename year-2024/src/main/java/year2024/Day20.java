package year2024;

import lib.Coordinate;
import lib.Day;
import lib.Dijkstra;
import lib.Dijkstra.CharMatrix;
import lib.Direction;
import lib.Matrix;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day20 extends Day {

    private char[][] map;
    private Coordinate start;
    private Coordinate end;
    private int raceTime;
    private List<Coordinate> raceTrack;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        map = Matrix.matrix(input.toList());
        start = Matrix.findChar(map, 'S');
        end = Matrix.findChar(map, 'E');

        Dijkstra<CharMatrix> dijkstra = new Dijkstra<>(
                new CharMatrix(map, coordinate -> coordinate.at(map) != '#'),
                start,
                end
        );

        raceTime = dijkstra.traverse().orElseThrow();
        raceTrack = dijkstra.getLowestCostPath();
    }

    @Override
    protected Object part1(Stream<String> input) {
        Set<Coordinate> cheats = raceTrack.parallelStream()
                .mapMulti((Coordinate c, Consumer<Coordinate> consumer) -> {
                    for (Direction direction : Direction.ALL) {
                        Coordinate cheatCandidate = c.tryMove(map, direction);
                        if (cheatCandidate == null || cheatCandidate.at(map) != '#') {
                            continue;
                        }

                        Coordinate nextAfterCheatCandidate = cheatCandidate.tryMove(map, direction);
                        if (nextAfterCheatCandidate == null) {
                            continue;
                        }

                        if (nextAfterCheatCandidate.at(map) == '.' || nextAfterCheatCandidate.at(map) == 'E') {
                            consumer.accept(cheatCandidate);
                        }
                    }
                })
                .collect(Collectors.toUnmodifiableSet());

        ThreadLocal<char[][]> cheatMaps = ThreadLocal.withInitial(() -> Matrix.deepClone(this.map));

        List<Integer> cheatedRacesFasterThanNormal = cheats.parallelStream()
                .map(cheat -> {
                    char[][] cheatMap = cheatMaps.get();
                    char old = cheat.at(map);
                    cheat.set(cheatMap, '.');

                    Dijkstra<CharMatrix> cheatDijkstra = new Dijkstra<>(
                            new CharMatrix(cheatMap, coordinate -> coordinate.at(cheatMap) != '#'),
                            start,
                            end
                    );

                    int cheatRaceTime = cheatDijkstra.traverse().orElseThrow();

                    cheat.set(cheatMap, old);

                    return cheatRaceTime;
                })
                .filter(cheatRaceTime -> cheatRaceTime < raceTime)
                .toList();

        return cheatedRacesFasterThanNormal
                .parallelStream()
                .filter(cheatRaceTime -> raceTime - cheatRaceTime >= 100)
                .count(); // Your puzzle answer was 1395
    }
}
