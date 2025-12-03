package year2025;

import lib.Common;
import lib.Day;
import lib.Pair;
import lib.Parse;

import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day2 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        return Parse.splitOnComma(input.findFirst().orElseThrow())
                .map(range -> {
                    String[] arr = range.split("-");
                    return Pair.of(Long.parseLong(arr[0]), Long.parseLong(arr[1]));
                })
                .mapMultiToLong((p, consumer) -> {
                    for (long v = p.first(); v <= p.second(); v++) {
                        consumer.accept(v);
                    }
                })
                .filter(Day2::isInvalidIdPart1)
                .sum(); // Your puzzle answer was 19128774598.
    }

    private static boolean isInvalidIdPart1(long id) {
        int n = Common.numberOfDigits(id);
        if (n % 2 != 0) {
            return false;
        }

        Pair<Long, Long> split = Common.splitInTwo(id);
        return split.first().equals(split.second());
    }
}
