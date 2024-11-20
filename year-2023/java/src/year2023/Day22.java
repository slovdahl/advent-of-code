package year2023;

import com.google.common.collect.ContiguousSet;
import lib.Day;
import lib.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class Day22 extends Day {

    private List<Brick> bricks;
    private Map<Integer, List<Brick>> bricksPerFirstZ;
    private Map<Integer, List<Brick>> bricksPerLastZ;

    @Override
    protected void prepare(Stream<String> input) {
        Stream<String> sampleInput = """
                1,0,1~1,2,1
                0,0,2~2,0,2
                0,2,3~2,2,3
                0,0,4~0,2,4
                2,0,5~2,2,5
                0,1,6~2,1,6
                1,1,8~1,1,9
                """.lines();

        List<Brick> originalBricks = input
                .map(line -> line.split("~"))
                .map(endPointsArray -> Pair.of(endPointsArray[0].split(","), endPointsArray[1].split(",")))
                .map(pair -> new Brick(
                        ContiguousSet.closed(Integer.parseInt(pair.first()[0]), Integer.parseInt(pair.second()[0])),
                        ContiguousSet.closed(Integer.parseInt(pair.first()[1]), Integer.parseInt(pair.second()[1])),
                        ContiguousSet.closed(Integer.parseInt(pair.first()[2]), Integer.parseInt(pair.second()[2]))
                ))
                .sorted(comparing(brick -> brick.z().first()))
                .toList();

        bricks = settleAll(originalBricks).first();

        bricksPerFirstZ = bricks.stream()
                .collect(groupingBy(brick -> brick.z().first(), LinkedHashMap::new, toList()));

        bricksPerLastZ = bricks.stream()
                .collect(groupingBy(brick -> brick.z().last(), LinkedHashMap::new, toList()));
    }

    @Override
    protected Long part1(Stream<String> input) throws Exception {
        return bricks.stream()
                .filter(brick -> getBricksSupportingThis(brick, bricksPerFirstZ, bricksPerLastZ).isEmpty())
                .count(); // Your puzzle answer was 501
    }

    @Override
    protected Integer part2(Stream<String> input) throws Exception {
        return bricks.stream()
                .filter(brick -> !getBricksSupportingThis(brick, bricksPerFirstZ, bricksPerLastZ).isEmpty())
                .mapToInt(brick -> {
                    List<Brick> b = new ArrayList<>(bricks);
                    b.remove(brick);

                    return settleAll(b).second();
                })
                .sum(); // Your puzzle answer was 80948
    }

    private static Pair<List<Brick>, Integer> settleAll(List<Brick> bricks) {
        Map<Integer, List<Brick>> bricksPerLastZ = bricks.stream()
                .collect(groupingBy(brick -> brick.z().last(), LinkedHashMap::new, toList()));

        bricks = new ArrayList<>(bricks);

        int numberSettled = 0;

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            if (brick.z().first() == 1) {
                continue;
            }

            boolean settled = false;
            outer:
            for (Integer zCandidate : ContiguousSet.closed(1, brick.z().first() - 1).reversed()) {
                for (Brick candidate : bricksPerLastZ.getOrDefault(zCandidate, List.of())) {
                    if (candidate.intersects(Brick::x, brick) &&
                            candidate.intersects(Brick::y, brick)) {

                        if (brick.z().first() == candidate.z().last() + 1) {
                            settled = true;
                            break outer;
                        }

                        bricksPerLastZ.get(brick.z().last()).remove(brick);

                        Brick updatedBrick = brick.settleAbove(candidate);
                        bricks.set(i, updatedBrick);

                        bricksPerLastZ.computeIfAbsent(updatedBrick.z().last(), k -> new ArrayList<>())
                                .add(updatedBrick);

                        settled = true;
                        numberSettled++;
                        break outer;
                    }
                }
            }

            if (!settled) {
                bricksPerLastZ.get(brick.z().last()).remove(brick);

                Brick updatedBrick = brick.settleAt(1);
                bricks.set(i, updatedBrick);

                bricksPerLastZ.computeIfAbsent(updatedBrick.z().last(), k -> new ArrayList<>())
                        .add(updatedBrick);

                numberSettled++;
            }
        }

        return Pair.of(bricks, numberSettled);
    }

    private static List<Brick> getBricksSupportingThis(Brick brick, Map<Integer, List<Brick>> bricksPerFirstZ, Map<Integer, List<Brick>> bricksPerLastZ) {
        List<Brick> b = bricksPerFirstZ.getOrDefault(brick.z().last() + 1, List.of());

        return b.stream()
                .filter(brickAbove -> brickAbove.intersects(Brick::x, brick) || brickAbove.intersects(Brick::y, brick))
                .filter(brickAbove -> {
                    int zToMatch = brickAbove.z().first() - 1;
                    List<Brick> bricksOnRowBelow = bricksPerLastZ.get(zToMatch);

                    if (bricksOnRowBelow.isEmpty()) {
                        return true;
                    }

                    return bricksOnRowBelow.stream()
                            .filter(candidate -> candidate != brick)
                            .noneMatch(candidate ->
                                    candidate.intersects(Brick::x, brickAbove) &&
                                            candidate.intersects(Brick::y, brickAbove)
                            );
                })
                .toList();
    }

    private record Brick(ContiguousSet<Integer> x,
                         ContiguousSet<Integer> y,
                         ContiguousSet<Integer> z) {

        private boolean intersects(Function<Brick, ContiguousSet<Integer>> fn, Brick other) {
            return !fn.apply(this).intersection(fn.apply(other)).isEmpty();
        }

        Brick settleAbove(Brick other) {
            return settleAt(other.z().last() + 1);
        }

        Brick settleAt(int z) {
            return new Brick(
                    x(),
                    y(),
                    ContiguousSet.closed(
                            z,
                            z + z().size() - 1
                    )
            );
        }
    }
}
