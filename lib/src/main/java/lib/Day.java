package lib;

import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public abstract class Day {

    private static final AtomicReference<Stopwatch> PREPARE_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

    public final void run(int day) throws Exception {
        int year = Integer.parseInt(getClass().getPackageName().substring(4));

        System.out.printf("""
                ==================
                |                |
                |  Year %4d     |
                |  Day %2d        |
                |                |
                ==================%n
                """, year, day);

        runPrepare(getInput(year, day));
        runPart1(getInput(year, day));
        runPart2(getInput(year, day));
    }

    private void runPrepare(Stream<String> input) throws Exception {
        timePrepare();

        prepare(input);

        result(Part.PREPARE, "OK");
    }

    private void runPart1(Stream<String> input) throws Exception {
        timePart1();

        result(Part.PART_1, part1(input));
    }

    private void runPart2(Stream<String> input) throws Exception {
        timePart2();

        try {
            result(Part.PART_2, part2(input));
        } catch (UnsupportedOperationException ignore) {
            // ignore, not yet implemented
        }
    }

    protected abstract Mode mode();

    protected void prepare(Stream<String> input) throws Exception {
    }

    protected abstract Object part1(Stream<String> input) throws Exception;

    protected Object part2(Stream<String> input) throws Exception {
        throw new UnsupportedOperationException();
    }

    private Stream<String> getInput(int year, int day) throws IOException, InterruptedException {
        return switch (mode()) {
            case SAMPLE_INPUT -> readSampleInputLinesFor(year, day);
            case REAL_INPUT -> readInputLinesFor(year, day);
        };
    }

    private void timePrepare() {
        PREPARE_TIMING.set(Stopwatch.createStarted());
    }

    private void timePart1() {
        PART1_TIMING.set(Stopwatch.createStarted());
    }

    private void timePart2() {
        PART2_TIMING.set(Stopwatch.createStarted());
    }

    private void result(Part part, @Nullable Object result) {
        Stopwatch stopwatch = switch (part) {
            case PREPARE -> PREPARE_TIMING.get().stop();
            case PART_1 -> PART1_TIMING.get().stop();
            case PART_2 -> PART2_TIMING.get().stop();
        };

        String partString = switch (part) {
            case PREPARE -> "Preparation";
            case PART_1 -> "Part 1";
            case PART_2 -> "Part 2";
        };

        String resultString = Strings.nullToEmpty(String.valueOf(result));

        int dividerLength = Math.max(19, 10 + resultString.length());
        int headerPrefixLength = (dividerLength - partString.length() - 2) / 2;
        int headerPostfixLength = headerPrefixLength;
        if (headerPrefixLength + headerPostfixLength + partString.length() + 2 < dividerLength) {
            headerPostfixLength++;
        }

        if (part == Part.PREPARE) {
            System.out.printf("""
                            %4$s %1$s %5$s
                             Time    %2$s
                            %6$s
                            %n""",
                    partString,
                    stopwatch,
                    result,
                    Strings.repeat("=", headerPrefixLength),
                    Strings.repeat("=", headerPostfixLength),
                    Strings.repeat("-", dividerLength));
        } else {
            System.out.printf("""
                            %4$s %1$s %5$s
                             Time    %2$s
                             Result  %3$s
                            %6$s
                            %n""",
                    partString,
                    stopwatch,
                    result,
                    Strings.repeat("=", headerPrefixLength),
                    Strings.repeat("=", headerPostfixLength),
                    Strings.repeat("-", dividerLength));
        }
    }

    private static Stream<String> readSampleInputLinesFor(int year, int day) throws IOException {
        Path path = Path.of(System.getProperty("user.dir"))
                .resolve("input/" + day + "/sample");

        if (!path.toFile().exists()) {
            throw new IllegalStateException("Sample file not found for " + year + "/ " + day + ": " + path);
        }

        return Files.lines(path);
    }

    private static Stream<String> readInputLinesFor(int year, int day) throws IOException, InterruptedException {
        Path userDir = Path.of(System.getProperty("user.dir"));
        Path path = userDir;
        Path tokenPath;
        if (Files.exists(userDir.resolve("settings.gradle"))) {
            path = path.resolve("year-" + year);
            tokenPath = userDir.resolve(".aoc-token");
        } else {
            tokenPath = userDir.getParent().resolve(".aoc-token");
        }

        Path filePath = path.resolve("input/" + day + "/input");
        if (!filePath.toFile().exists()) {
            Downloader.download(year, day, filePath, Files.readString(tokenPath).trim());
        }

        Path samplePath = path.resolve("input/" + day + "/sample");
        if (!samplePath.toFile().exists()) {
            Files.createFile(samplePath);
        }

        return Files.lines(filePath);
    }

    private enum Part {
        PREPARE,
        PART_1,
        PART_2
    }

    public enum Mode {
        SAMPLE_INPUT,
        REAL_INPUT
    }
}
