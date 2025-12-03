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

    @Override
    protected Object part2(Stream<String> input) {
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
                .filter(Day2::isInvalidIdPart2)
                .sum(); // Your puzzle answer was 21932258645.
    }

    private static boolean isInvalidIdPart1(long id) {
        int n = Common.numberOfDigits(id);
        if (n % 2 != 0) {
            return false;
        }

        Pair<Long, Long> split = Common.splitInTwo(id);
        return split.first().equals(split.second());
    }

    private static boolean isInvalidIdPart2(long id) {
        int n = Common.numberOfDigits(id);
        if (n == 1) {
            return false;
        }

        if (n % 2 == 0 && n > 2) {
            Pair<Long, Long> split = Common.splitInTwo(id);
            if (split.first().equals(split.second())) {
                return true;
            }
        }

        String idStr = String.valueOf(id);
        if (n % 3 == 0 && n > 3) {
            String secondPart = part(idStr, 3, 2);

            if (part(idStr, 3, 1).equals(secondPart) &&
                    secondPart.equals(part(idStr, 3, 3))) {

                return true;
            }
        }

        if (n % 4 == 0 && n > 4) {
            String secondPart = part(idStr, 4, 2);
            String thirdPart = part(idStr, 4, 3);

            if (part(idStr, 4, 1).equals(secondPart) &&
                    secondPart.equals(thirdPart) &&
                    thirdPart.equals(part(idStr, 4, 4))) {
                return true;
            }
        }

        if (n % 5 == 0 && n > 5) {
            String secondPart = part(idStr, 5, 2);
            String thirdPart = part(idStr, 5, 3);
            String fourthPart = part(idStr, 5, 4);

            if (part(idStr, 5, 1).equals(secondPart) &&
                    secondPart.equals(thirdPart) &&
                    thirdPart.equals(fourthPart) &&
                    fourthPart.equals(part(idStr, 5, 5))) {
                return true;
            }
        }

        if (n % 6 == 0 && n > 6) {
            String secondPart = part(idStr, 6, 2);
            String thirdPart = part(idStr, 6, 3);
            String fourthPart = part(idStr, 6, 4);
            String fifthPart = part(idStr, 6, 5);

            if (part(idStr, 6, 1).equals(secondPart) &&
                    secondPart.equals(thirdPart) &&
                    thirdPart.equals(fourthPart) &&
                    fourthPart.equals(fifthPart) &&
                    fifthPart.equals(part(idStr, 6, 6))) {
                return true;
            }
        }

        char firstDigit = idStr.charAt(0);
        for (int i = 1; i < idStr.length(); i++) {
            if (firstDigit != idStr.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private static String part(String s, int c, int n) {
        return s.substring((s.length() / c) * (n - 1), (s.length() / c) * n);
    }
}
