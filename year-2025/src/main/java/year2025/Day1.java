package year2025;

import lib.Day;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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

    @Override
    protected Object part2(Stream<String> input) {
        int startingPosition = 50;

        return input
                .map(line -> {
                    int number = Integer.parseInt(line.substring(1));
                    return switch (line.charAt(0)) {
                        case 'L' -> number * -1;
                        case 'R' -> number;
                        default -> throw new IllegalStateException("Unexpected value: " + line);
                    };
                })
                .collect(
                        () -> new State(startingPosition),
                        State::move,
                        (p1, p2) -> p1.password().addAndGet(p2.password().get())
                )
                .password()
                .get(); // Your puzzle answer was 5815.
    }

    private record State(AtomicInteger position, AtomicInteger password) {
        private State(int initialPosition) {
            this(
                    new AtomicInteger(initialPosition),
                    new AtomicInteger(0)
            );
        }

        void move(int move) {
            int currentPosition = position.get();

            int fullRotations = Math.abs(move) / 100;
            password().addAndGet(fullRotations);

            int newPosition;
            if (move > 0) {
                newPosition = (currentPosition + move) % 100;
                if (newPosition != 0 && newPosition < currentPosition) {
                    password().incrementAndGet();
                }
            } else {
                newPosition = (currentPosition + move) % 100;
                if (newPosition < 0) {
                    newPosition += 100;
                    if (currentPosition != 0 && newPosition > currentPosition) {
                        password().incrementAndGet();
                    }
                }
            }

            assert newPosition >= 0 && newPosition <= 99;

            position().set(newPosition);

            if (newPosition == 0) {
                password().incrementAndGet();
            }
        }
    }
}
