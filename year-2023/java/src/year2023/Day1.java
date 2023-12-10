package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static year2023.Common.readInputLinesForDay;

public class Day1 extends Day {

    /**
     * As they're making the final adjustments, they discover that their calibration document (your
     * puzzle input) has been amended by a very young Elf who was apparently just excited to show
     * off her art skills. Consequently, the Elves are having trouble reading the values on the
     * document.
     *
     * The newly-improved calibration document consists of lines of text; each line originally
     * contained a specific calibration value that the Elves now need to recover. On each line, the
     * calibration value can be found by combining the first digit and the last digit (in that
     * order) to form a single two-digit number.
     *
     * For example:
     *
     * 1abc2
     * pqr3stu8vwx
     * a1b2c3d4e5f
     * treb7uchet
     *
     * In this example, the calibration values of these four lines are 12, 38, 15, and 77. Adding
     * these together produces 142.
     *
     * Consider your entire calibration document. What is the sum of all of the calibration values?
     *
     * Your puzzle answer was 54561.
     */
    @Override
    Object part1(Stream<String> input) throws IOException {
        return input
                .map(l -> {
                            List<String> lineDigits = l.chars()
                                    .filter(Character::isDigit)
                                    .map(c -> Character.digit(c, 10))
                                    .mapToObj(String::valueOf)
                                    .toList();

                            return Integer.parseInt(lineDigits.getFirst() + lineDigits.getLast());
                        }
                )
                .mapToInt(v -> v)
                .sum();
    }

    /**
     * Your calculation isn't quite right. It looks like some of the digits are actually spelled
     * out with letters: one, two, three, four, five, six, seven, eight, and nine also count as
     * valid "digits".
     *
     * Equipped with this new information, you now need to find the real first and last digit on
     * each line. For example:
     *
     * two1nine
     * eightwothree
     * abcone2threexyz
     * xtwone3four
     * 4nineeightseven2
     * zoneight234
     * 7pqrstsixteen
     *
     * In this example, the calibration values are 29, 83, 13, 24, 42, 14, and 76. Adding these
     * together produces 281.
     *
     * What is the sum of all of the calibration values?
     *
     * Your puzzle answer was 54076.
     */
    @Override
    Object part2(Stream<String> input) throws IOException {
        return input
                .map(l -> {
                            List<Integer> digitPerIndex = new ArrayList<>(l.length());

                            char[] a = l.toCharArray();
                            for (int i = 0; i < a.length; i++) {
                                char c = a[i];
                                if (Character.isDigit(c)) {
                                    digitPerIndex.add(Character.digit(c, 10));
                                } else if (Character.isLetter(c)) {
                                    digitPerIndex.add(switch (c) {
                                        case 'o' -> {
                                            // one
                                            if (a.length > i + 2) {
                                                if (a[i + 1] == 'n' && a[i + 2] == 'e') {
                                                    yield 1;
                                                }
                                            }
                                            yield -1;
                                        }
                                        case 't' -> {
                                            // two, three
                                            if (a.length > i + 2) {
                                                if (a[i + 1] == 'w' && a[i + 2] == 'o') {
                                                    yield 2;
                                                }
                                            }
                                            if (a.length > i + 4) {
                                                if (a[i + 1] == 'h' && a[i + 2] == 'r' &&
                                                        a[i + 3] == 'e' && a[i + 4] == 'e') {
                                                    yield 3;
                                                }
                                            }
                                            yield -1;
                                        }
                                        case 's' -> {
                                            // six, seven
                                            if (a.length > i + 2) {
                                                if (a[i + 1] == 'i' && a[i + 2] == 'x') {
                                                    yield 6;
                                                }
                                            }
                                            if (a.length > i + 4) {
                                                if (a[i + 1] == 'e' && a[i + 2] == 'v' && a[i + 3] == 'e' && a[i + 4] == 'n') {
                                                    yield 7;
                                                }
                                            }
                                            yield -1;
                                        }
                                        case 'f' -> {
                                            // four, five
                                            if (a.length > i + 3) {
                                                if (a[i + 1] == 'o' && a[i + 2] == 'u' && a[i + 3] == 'r') {
                                                    yield 4;
                                                }
                                                if (a[i + 1] == 'i' && a[i + 2] == 'v' && a[i + 3] == 'e') {
                                                    yield 5;
                                                }
                                            }
                                            yield -1;
                                        }
                                        case 'n' -> {
                                            // nine
                                            if (a.length > i + 3) {
                                                if (a[i + 1] == 'i' && a[i + 2] == 'n' && a[i + 3] == 'e') {
                                                    yield 9;
                                                }
                                            }
                                            yield -1;
                                        }
                                        case 'e' -> {
                                            // eight
                                            if (a.length > i + 4) {
                                                if (a[i + 1] == 'i' && a[i + 2] == 'g' && a[i + 3] == 'h' && a[i + 4] == 't') {
                                                    yield 8;
                                                }
                                            }
                                            yield -1;
                                        }
                                        default -> -1;
                                    });
                                }
                            }

                            List<String> lineDigits = digitPerIndex
                                    .stream()
                                    .filter(n -> n > 0)
                                    .map(String::valueOf)
                                    .toList();

                            if (lineDigits.isEmpty()) {
                                return 0;
                            }

                            return Integer.parseInt(lineDigits.getFirst() + lineDigits.getLast());
                        }
                )
                .mapToInt(v -> v)
                .sum();
    }
}
