package year2024;

import lib.Day;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day3 extends Day {

    private static final Pattern DIGIT_PATTERN = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");

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
                int n1 = Integer.parseInt(matcher.group(1));
                int n2 = Integer.parseInt(matcher.group(2));
                sum += n1 * n2;
            }
        }

        return sum;
    }
}
