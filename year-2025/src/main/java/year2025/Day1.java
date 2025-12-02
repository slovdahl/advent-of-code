package year2025;

import lib.Day;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day1 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Integer> moves = input
                .map(line -> {
                    int number = Integer.parseInt(line.substring(1));
                    return switch (line.charAt(0)) {
                        case 'L' -> 100 - (number % 100);
                        case 'R' -> number % 100;
                        default -> throw new IllegalStateException("Unexpected value: " + line);
                    };
                })
                .toList();

        int position = 50;
        int password = 0;
        for (Integer move : moves) {
            if (position == 0) {
                password++;
            }

            position = Math.abs((position + move) % 100);
        }

        if (position == 0) {
            password++;
        }

        return password; // Your puzzle answer was 1018.
    }
}
