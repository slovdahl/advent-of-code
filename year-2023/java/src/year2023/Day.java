package year2023;

import com.google.common.base.Stopwatch;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static year2023.Common.readInputLinesForDay;

public abstract class Day {

    private static final AtomicReference<Stopwatch> PREPARE_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

    final void run(int day) throws Exception {
        System.out.printf("""
                ==================
                |                |
                |  Day %2d        |
                |                |
                ==================%n
                """, day);

        runPrepare(readInputLinesForDay(day));
        runPart1(readInputLinesForDay(day));
        runPart2(readInputLinesForDay(day));
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

    void prepare(Stream<String> input) throws Exception {
    }

    abstract Object part1(Stream<String> input) throws Exception;

    Object part2(Stream<String> input) throws Exception {
        throw new UnsupportedOperationException();
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
}
