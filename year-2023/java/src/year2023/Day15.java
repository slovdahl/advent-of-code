package year2023;

import java.io.IOException;
import java.util.stream.Stream;

import static year2023.Common.splitOnComma;

@SuppressWarnings("unused")
public class Day15 extends Day {

    @Override
    Integer part1(Stream<String> input) throws IOException {
        return splitOnComma(input.findFirst().get())
                .map(Day15::hash)
                .mapToInt(v -> v)
                .sum();
    }

    private static int hash(String input) {
        return input.chars()
                .reduce(0, (left, right) -> ((left + right) * 17) % 256);
    }
}
