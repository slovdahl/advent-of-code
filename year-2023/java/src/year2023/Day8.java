package year2023;

import lib.Common;
import lib.Day;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;

@SuppressWarnings("unused")
public class Day8 extends Day {

    @Override
    protected Object part1(Stream<String> rawInput) throws Exception {
        var input = rawInput.collect(toList());

        String path = input.removeFirst();

        Pattern linePattern = Pattern.compile("^([A-Z]{3}) = \\(([A-Z]{3}), ([A-Z]{3})\\)$");

        Map<String, Choices> choices = input.stream()
                .filter(line -> !line.isBlank())
                .map(linePattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> Map.entry(matcher.group(1), new Choices(matcher.group(2), matcher.group(3))))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        String currentNode = "AAA";

        long steps = 0;
        while (!currentNode.equals("ZZZ")) {
            Choices c = choices.get(currentNode);

            int choice = (int) (steps % path.length());
            steps++;

            currentNode = switch (path.charAt(choice)) {
                case 'L' -> c.left;
                case 'R' -> c.right;
                default -> throw new IllegalStateException();
            };
        }

        return steps; // Your puzzle answer was 18023
    }

    @Override
    protected Object part2(Stream<String> rawInput) throws Exception {
        var input = rawInput.collect(toList());

        String path = input.removeFirst();

        Pattern linePattern = Pattern.compile("^([0-9A-Z]{3}) = \\(([0-9A-Z]{3}), ([0-9A-Z]{3})\\)$");

        Map<String, Choices> choices = input.stream()
                .filter(line -> !line.isBlank())
                .map(linePattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> Map.entry(matcher.group(1), new Choices(matcher.group(2), matcher.group(3))))
                .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        return choices
                .keySet()
                .stream()
                .filter(node -> node.charAt(2) == 'A')
                .map(currentNode -> {
                    for (long i = 0; i < Long.MAX_VALUE; i++) {
                        int choiceIndex = (int) (i % path.length());
                        char choice = path.charAt(choiceIndex);

                        Choices c = choices.get(currentNode);
                        currentNode = switch (choice) {
                            case 'L' -> c.left;
                            case 'R' -> c.right;
                            default -> throw new IllegalStateException();
                        };

                        if (currentNode.charAt(2) == 'Z') {
                            return i + 1;
                        }
                    }

                    throw new IllegalStateException();
                })
                .mapToLong(v -> v)
                .reduce(Common::lcm)
                .orElseThrow(); // Your puzzle answer was 14449445933179
    }

    record Choices(String left, String right) {
    }
}
