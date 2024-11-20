package lib;

import com.google.common.base.Stopwatch;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static lib.Common.readInputLinesFor;

public abstract class Day {

    private static final AtomicReference<Stopwatch> PREPARE_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

    public final void run(int day) throws Exception {
        int year = Integer.parseInt(getClass().getPackageName().substring(4));

        System.out.printf("""
                ==================
                |                |
                |  Day %2d        |
                |                |
                ==================%n
                """, day);

        runPrepare(readInputLinesFor(year, day));
        runPart1(readInputLinesFor(year, day));
        runPart2(readInputLinesFor(year, day));
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

    protected void prepare(Stream<String> input) throws Exception {
    }

   protected abstract Object part1(Stream<String> input) throws Exception;

    protected Object part2(Stream<String> input) throws Exception {
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