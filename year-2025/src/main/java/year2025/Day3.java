package year2025;

import lib.Day;

import java.util.stream.Stream;

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
}
