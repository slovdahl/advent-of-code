package year2025;

import lib.Day;

import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("unused")
public class Day3 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        return input
                .mapToInt(line -> {
                    char biggestNumber1 = '_';
                    char biggestNumber2 = '_';

                    for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);
                        if (biggestNumber1 == '_') {
                            biggestNumber1 = c;
                            continue;
                        } else if (c > biggestNumber1 && i < line.length() - 1) {
                            biggestNumber1 = c;
                            biggestNumber2 = '_';
                            continue;
                        }

                        if (biggestNumber2 == '_' || c > biggestNumber2) {
                            biggestNumber2 = c;
                        }
                    }

                    return Character.getNumericValue(biggestNumber1) * 10 + Character.getNumericValue(biggestNumber2);
                })
                .sum(); // Your puzzle answer was 16993.
    }

    @Override
    protected Object part2(Stream<String> input) {
        return input
                .mapToLong(line -> {
                    char[] numbers = new char[]{'_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_', '_'};

                    for (int i = 0; i < line.length(); i++) {
                        char c = line.charAt(i);
                        if (numbers[0] == '_' || (c > numbers[0] && i < line.length() - 11)) {
                            numbers[0] = c;
                            clear(numbers, 1, 11);
                            continue;
                        }

                        if (numbers[1] == '_' || (c > numbers[1] && i < line.length() - 10)) {
                            numbers[1] = c;
                            clear(numbers, 2, 11);
                            continue;
                        }

                        if (numbers[2] == '_' || (c > numbers[2] && i < line.length() - 9)) {
                            numbers[2] = c;
                            clear(numbers, 3, 11);
                            continue;
                        }

                        if (numbers[3] == '_' || (c > numbers[3] && i < line.length() - 8)) {
                            numbers[3] = c;
                            clear(numbers, 4, 11);
                            continue;
                        }

                        if (numbers[4] == '_' || (c > numbers[4] && i < line.length() - 7)) {
                            numbers[4] = c;
                            clear(numbers, 5, 11);
                            continue;
                        }

                        if (numbers[5] == '_' || (c > numbers[5] && i < line.length() - 6)) {
                            numbers[5] = c;
                            clear(numbers, 6, 11);
                            continue;
                        }

                        if (numbers[6] == '_' || (c > numbers[6] && i < line.length() - 5)) {
                            numbers[6] = c;
                            clear(numbers, 7, 11);
                            continue;
                        }

                        if (numbers[7] == '_' || (c > numbers[7] && i < line.length() - 4)) {
                            numbers[7] = c;
                            clear(numbers, 8, 11);
                            continue;
                        }

                        if (numbers[8] == '_' || (c > numbers[8] && i < line.length() - 3)) {
                            numbers[8] = c;
                            clear(numbers, 9, 11);
                            continue;
                        }

                        if (numbers[9] == '_' || (c > numbers[9] && i < line.length() - 2)) {
                            numbers[9] = c;
                            clear(numbers, 10, 11);
                            continue;
                        }

                        if (numbers[10] == '_' || (c > numbers[10] && i < line.length() - 1)) {
                            numbers[10] = c;
                            clear(numbers, 11, 11);
                            continue;
                        }

                        if (numbers[11] == '_' || (c > numbers[11] && i < line.length() - 0)) {
                            numbers[11] = c;
                            continue;
                        }
                    }

                    long sum = 0;
                    for (int i = 0; i < numbers.length; i++) {
                        int numericValue = Character.getNumericValue(numbers[i]);
                        checkState(numericValue >= 0, "unexpected numeric value %s", numericValue);
                        sum += numericValue * Math.powExact(10L, 11 - i);
                    }

                    return sum;
                })
                .sum(); // Your puzzle answer was 168617068915447.
    }

    private static void clear(char[] numbers, int from, int to) {
        for (int i = from; i <= to; i++) {
            numbers[i] = '_';
        }
    }
}
