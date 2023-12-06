package year2023;

import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Common {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

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

    public static void startPart1() {
        PART1_TIMING.set(Stopwatch.createStarted());
    }

    public static void startPart2() {
        PART2_TIMING.set(Stopwatch.createStarted());
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void result(int part, Object result) {
        Stopwatch stopwatch = switch (part) {
            case 1 -> PART1_TIMING.get().stop();
            case 2 -> PART2_TIMING.get().stop();
            default -> throw new IllegalArgumentException("Unknown part: " + part);
        };

        System.out.printf("""
                ======================================================================
                 Part    %d
                 Time    %s
                 Result  %s
                ======================================================================
                %n""", part, stopwatch, result);
    }
}
