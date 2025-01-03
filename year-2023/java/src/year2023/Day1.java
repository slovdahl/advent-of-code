package year2023;

import lib.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day1 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) throws IOException {
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
                .sum(); // Your puzzle answer was 54561.
    }

    @Override
    protected Object part2(Stream<String> input) throws IOException {
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
                .sum(); // Your puzzle answer was 54076
    }
}
