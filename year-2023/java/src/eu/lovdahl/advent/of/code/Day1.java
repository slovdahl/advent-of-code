package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.util.List;

import static eu.lovdahl.advent.of.code.Common.readInputLinesForDay;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
class Day1 {

    public static void main(String[] args) throws IOException {
        part1();
    }

    public static void part1() throws IOException {
        var result = readInputLinesForDay(1)
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

        System.out.println("Part 1: " + result);
    }
}
