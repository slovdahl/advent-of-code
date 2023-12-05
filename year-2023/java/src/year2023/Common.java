package year2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Common {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    public static Stream<String> readInputLinesForDay(int day) throws IOException {
        Path path = Path.of("year-2023/" + day + "/input");

        if (!path.toFile().exists()) {
            path = Path.of(day + "/input");
        }

        return Files.lines(path);
    }

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
