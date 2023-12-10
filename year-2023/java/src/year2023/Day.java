package year2023;

import com.google.common.base.Stopwatch;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public abstract class Day {

    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

    final void runPart1(Stream<String> input) throws Exception {
        timePart1();

        result(1, part1(input));
    }

    final void runPart2(Stream<String> input) throws Exception {
        timePart2();

        try {
            result(2, part2(input));
        } catch (UnsupportedOperationException ignore) {
            // ignore, not yet implemented
        }
    }

    abstract Object part1(Stream<String> input) throws Exception;

    Object part2(Stream<String> input) throws Exception {
        throw new UnsupportedOperationException();
    }

    private void timePart1() {
        PART1_TIMING.set(Stopwatch.createStarted());
    }

    private void timePart2() {
        PART2_TIMING.set(Stopwatch.createStarted());
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void result(int part, Object result) {
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
