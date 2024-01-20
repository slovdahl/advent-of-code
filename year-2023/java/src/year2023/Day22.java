package year2023;

import com.google.common.collect.ContiguousSet;
import year2023.tools.Pair;

import java.util.ArrayList;
import java.util.Iterator;
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

    @Override
    Integer part1(Stream<String> input) throws Exception {
        Stream<String> sampleInput = """
                1,0,1~1,2,1
                0,0,2~2,0,2
                0,2,3~2,2,3
                0,0,4~0,2,4
                2,0,5~2,2,5
                0,1,6~2,1,6
                1,1,8~1,1,9
                """.lines();

        List<Brick> bricks = input
                .map(line -> line.split("~"))
                .map(endPointsArray -> Pair.of(endPointsArray[0].split(","), endPointsArray[1].split(",")))
                .map(pair -> new Brick(
                        ContiguousSet.closed(Integer.parseInt(pair.first()[0]), Integer.parseInt(pair.second()[0])),
                        ContiguousSet.closed(Integer.parseInt(pair.first()[1]), Integer.parseInt(pair.second()[1])),
                        ContiguousSet.closed(Integer.parseInt(pair.first()[2]), Integer.parseInt(pair.second()[2]))
                ))
                .sorted(comparing(brick -> brick.z().first()))
                .collect(toList());

        Map<Integer, List<Brick>> bricksPerLastZ = bricks.stream()
                .collect(groupingBy(brick -> brick.z().last(), LinkedHashMap::new, toList()));

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

                        bricksPerLastZ.get(brick.z().last()).remove(brick);

                        Brick updatedBrick = brick.settleAbove(candidate);
                        bricks.set(i, updatedBrick);

                        bricksPerLastZ.computeIfAbsent(updatedBrick.z().last(), k -> new ArrayList<>())
                                .add(updatedBrick);

                        settled = true;
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
            }
        }

        Map<Integer, List<Brick>> bricksPerFirstZ = bricks.stream()
                .collect(groupingBy(brick -> brick.z().first(), LinkedHashMap::new, toList()));

        int canBeDisintegrated = 0;

        for (Brick brick : bricks) {
            List<Brick> bricksThatThisSupport = new ArrayList<>(bricksPerFirstZ.getOrDefault(brick.z().last() + 1, List.of()));

            boolean holdsUpAtLeastOne = false;

            Iterator<Brick> iterator = bricksThatThisSupport.iterator();
            while (iterator.hasNext()) {
                Brick brickAbove = iterator.next();

                if (!brickAbove.intersects(Brick::x, brick) &&
                        !brickAbove.intersects(Brick::y, brick)) {

                    iterator.remove();
                    continue;
                }

                int zToMatch = brickAbove.z().first() - 1;
                List<Brick> bricksOnRowBelow = bricksPerLastZ.get(zToMatch);

                if (bricksOnRowBelow.isEmpty()) {
                    continue;
                }

                boolean hasSupportingBrick = bricksOnRowBelow.stream()
                        .filter(candidate -> candidate != brick)
                        .anyMatch(candidate ->
                                candidate.intersects(Brick::x, brickAbove) &&
                                        candidate.intersects(Brick::y, brickAbove)
                        );

                if (hasSupportingBrick) {
                    iterator.remove();
                }
            }

            if (bricksThatThisSupport.isEmpty()) {
                canBeDisintegrated++;
            }
        }

        return canBeDisintegrated; // Your puzzle answer was 501
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
