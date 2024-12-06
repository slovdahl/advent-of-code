package lib;

import com.google.common.base.Stopwatch;

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

        result("prepare", "OK");
    }

    private void runPart1(Stream<String> input) throws Exception {
        timePart1();

        result("1", part1(input));
    }

    private void runPart2(Stream<String> input) throws Exception {
        timePart2();

        try {
            result("2", part2(input));
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

    private void result(String part, Object result) {
        Stopwatch stopwatch = switch (part) {
            case "prepare" -> PREPARE_TIMING.get().stop();
            case "1" -> PART1_TIMING.get().stop();
            case "2" -> PART2_TIMING.get().stop();
            default -> throw new IllegalArgumentException("Unknown part: " + part);
        };

        System.out.printf("""
                ==========================================================
                 Part    %s
                 Time    %s
                 Result  %s
                ==========================================================
                %n""", part, stopwatch, result);
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

    public enum Mode {
        SAMPLE_INPUT,
        REAL_INPUT;
    }
}
