package year2024;

import lib.Common;
import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        List<Long> stones = Parse.longs(input.findFirst().orElseThrow());

        int blinks = 25;

        while (blinks > 0) {
            Map<Integer, Long> stonesToAdd = new LinkedHashMap<>();
            for (int i = 0; i < stones.size(); i++) {
                Long stone = stones.get(i);

                if (stone == 0L) {
                    stones.set(i, 1L);
                } else if (Common.numberOfDigits(stone) % 2 == 0) {
                    String oldValue = String.valueOf(stone);
                    long leftHalf = Long.parseLong(oldValue.substring(0, oldValue.length() / 2));
                    long rightHalf = Long.parseLong(oldValue.substring(oldValue.length() / 2));
                    stones.set(i, leftHalf);
                    stonesToAdd.put(i, rightHalf);
                } else {
                    stones.set(i, stone * 2024L);
                }
            }

            for (Map.Entry<Integer, Long> entry : new ArrayList<>(stonesToAdd.entrySet()).reversed()) {
                Integer index = entry.getKey();
                Long stone = entry.getValue();

                stones.add(index + 1, stone);
            }

            blinks--;
        }

        return stones.size(); // Your puzzle answer was 213625
    }
}
