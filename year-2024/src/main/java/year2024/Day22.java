package year2024;

import lib.Day;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day22 extends Day {

    private List<Long> initialSecretNumbers;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        initialSecretNumbers = input.map(Long::valueOf).toList();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return initialSecretNumbers.stream()
                .mapToLong(initialSecretNumber -> {
                    long secretNumber = initialSecretNumber;
                    for (int i = 0; i < 2000; i++) {
                        long step1 = ((secretNumber * 64) ^ secretNumber) % 16_777_216;
                        long step2 = ((step1 / 32) ^ step1) % 16_777_216;
                        long step3 = ((step2 * 2048) ^ step2) % 16_777_216;
                        secretNumber = step3;
                    }
                    return secretNumber;
                })
                .sum(); // Your puzzle answer was 16953639210
    }
}
