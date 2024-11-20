package lib;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class Parse {
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    public static List<Integer> ints(String input) {
        return Arrays.stream(SPACE_PATTERN.split(input.trim()))
                .map(Integer::parseInt)
                .toList();
    }

    public static List<Long> longs(String input) {
        return Arrays.stream(SPACE_PATTERN.split(input.trim()))
                .map(Long::parseLong)
                .toList();
    }
}
