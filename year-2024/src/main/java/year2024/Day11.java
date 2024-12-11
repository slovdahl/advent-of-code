package year2024;

import lib.Common;
import lib.Day;
import lib.Pair;
import lib.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day11 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        ArrayList<Long> stones = new ArrayList<>(Parse.longs(input.findFirst().orElseThrow()));

        int blinks = 25;

        while (blinks > 0) {
            List<Pair<Integer, Long>> stonesToAdd = new ArrayList<>();
            for (int i = 0; i < stones.size(); i++) {
                Long stone = stones.get(i);

                if (stone == 0L) {
                    stones.set(i, 1L);
                } else {
                    int numberOfDigits = Common.numberOfDigits(stone);
                    if (numberOfDigits % 2 == 0) {
                        long pow = (long) Math.pow(10, numberOfDigits / 2.0);
                        long leftHalf = stone / pow;
                        long rightHalf = stone - (leftHalf * pow);
                        stones.set(i, leftHalf);
                        stonesToAdd.add(Pair.of(i, rightHalf));
                    } else {
                        stones.set(i, stone * 2024L);
                    }
                }
            }

            stones.ensureCapacity(stones.size() + stonesToAdd.size());
            for (Pair<Integer, Long> entry : stonesToAdd.reversed()) {
                Integer index = entry.first();
                Long stone = entry.second();

                stones.add(index + 1, stone);
            }

            blinks--;
        }

        return stones.size(); // Your puzzle answer was 213625
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        List<Long> stones = Parse.longs(input.findFirst().orElseThrow());

        Map<Long, Long> stoneCounts = new HashMap<>();
        for (Long stone : stones) {
            stoneCounts.put(stone, 1L);
        }

        int blinks = 75;

        for (int i = 0; i < blinks; i++) {
            Map<Long, Long> stonesToAdd = new HashMap<>();

            for (var entry : stoneCounts.entrySet()) {
                Long stone = entry.getKey();
                Long count = entry.getValue();

                if (stone == 0L) {
                    stonesToAdd.merge(1L, count, Long::sum);
                } else {
                    int numberOfDigits = Common.numberOfDigits(stone);
                    if (numberOfDigits % 2 == 0) {
                        long pow = (long) Math.pow(10, numberOfDigits / 2.0);
                        long leftHalf = stone / pow;
                        long rightHalf = stone - (leftHalf * pow);
                        stonesToAdd.merge(leftHalf, count, Long::sum);
                        stonesToAdd.merge(rightHalf, count, Long::sum);
                    } else {
                        stonesToAdd.merge(stone * 2024L, count, Long::sum);
                    }
                }
            }

            stoneCounts.clear();
            stoneCounts.putAll(stonesToAdd);
        }

        return stoneCounts
                .values()
                .stream()
                .mapToLong(Long::valueOf)
                .sum(); // Your puzzle answer was 252442982856820
    }
}
