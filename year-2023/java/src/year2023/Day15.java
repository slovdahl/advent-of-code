package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static year2023.Common.splitOnComma;

@SuppressWarnings("unused")
public class Day15 extends Day {

    @Override
    Integer part1(Stream<String> input) throws IOException {
        return splitOnComma(input.findFirst().orElseThrow())
                .map(Day15::hash)
                .mapToInt(v -> v)
                .sum();
    }

    @Override
    Integer part2(Stream<String> input) throws Exception {
        Map<Integer, Box> boxes = IntStream.range(0, 256)
                .mapToObj(n -> new Box(n, new ArrayList<>(), new HashMap<>()))
                .collect(toMap(box -> box.number, box -> box));

        splitOnComma(input.findFirst().orElseThrow())
                .forEachOrdered(step -> {
                    int dashIndex = step.indexOf('-');
                    int equalIndex = -1;
                    if (dashIndex == -1) {
                        equalIndex = step.indexOf('=');
                    }

                    String label = step.substring(0, dashIndex != -1 ? dashIndex : equalIndex);
                    Box box = boxes.get(hash(label));
                    if (dashIndex != -1) {
                        box.lenses.remove(label);
                        box.focalLength.remove(label);
                    } else if (equalIndex != -1) {
                        if (!box.focalLength.containsKey(label)) {
                            box.lenses.addFirst(label);
                        }
                        box.focalLength.put(label, Integer.parseInt(step.substring(equalIndex + 1)));
                    } else {
                        throw new IllegalStateException();
                    }
                });

        return boxes.values().stream()
                .mapToInt(box -> {
                    int sum = 0;
                    int i = 1;
                    for (String lensLabel : box.lenses.reversed()) {
                        sum += (i++ * box.focalLength.get(lensLabel));
                    }

                    return (1 + box.number) * sum;
                })
                .sum(); // Your puzzle answer was 212449
    }

    private static int hash(String input) {
        return input.chars()
                .reduce(0, (previous, next) -> ((previous + next) * 17) % 256);
    }

    record Box(int number, List<String> lenses, Map<String, Integer> focalLength) {
    }
}
