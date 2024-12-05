package year2024;

import lib.Day;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day3 extends Day {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");
    private static final Pattern FULL_PATTERN = Pattern.compile("(do\\(\\)|don't\\(\\)|mul\\(([0-9]{1,3}),([0-9]{1,3})\\))");

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        int sum = 0;

        for (String line : input.toList()) {
            Matcher matcher = DIGIT_PATTERN.matcher(line);
            while (matcher.find()) {
                sum += Integer.parseInt(matcher.group(1)) * Integer.parseInt(matcher.group(2));
            }
        }

        return sum; // Your puzzle answer was 182619815
    }

    @Override
    protected Object part2(Stream<String> input) {
        int sum = 0;

        boolean enabled = true;
        for (String line : input.toList()) {
            Matcher matcher = FULL_PATTERN.matcher(line);

            while (matcher.find()) {
                if (matcher.group(1).equals("do()")) {
                    enabled = true;
                } else if (matcher.group(1).equals("don't()")) {
                    enabled = false;
                } else if (enabled) {
                    sum += Integer.parseInt(matcher.group(2)) * Integer.parseInt(matcher.group(3));
                }
            }
        }

        return sum; // Your puzzle answer was 80747545
    }
}
