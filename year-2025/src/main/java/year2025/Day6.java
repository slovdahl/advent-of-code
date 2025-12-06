package year2025;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class Day6 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<String> inputLines = input.collect(toList());
        String operatorsLine = inputLines.removeLast();
        List<String> operators = Parse.strings(operatorsLine);

        Map<Integer, Long> result = new HashMap<>(inputLines.size());
        for (String inputLine : inputLines) {
            List<Long> numbers = Parse.longs(inputLine);
            for (int i = 0; i < numbers.size(); i++) {
                Long value = numbers.get(i);
                if (!result.containsKey(i)) {
                    result.put(i, value);
                    continue;
                }

                Long current = result.get(i);
                String s = operators.get(i);

                result.put(i, switch (operators.get(i)) {
                    case "+" -> current + value;
                    case "*" -> current * value;
                    default -> throw new IllegalArgumentException("invalid operator: " + operators.get(i));
                });
            }
        }

        return result.values().stream()
                .mapToLong(v -> v)
                .sum(); // Your puzzle answer was 6891729672676.
    }

    @Override
    protected Object part2(Stream<String> input) {
        List<String> inputLines = input.collect(toList());
        int maxLineLength = inputLines.stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElseThrow();

        String operatorsLine = inputLines.removeLast();

        List<Operator> operators = new ArrayList<>();
        for (int i = 0; i < operatorsLine.length(); i++) {
            char ch = operatorsLine.charAt(i);
            if (ch == ' ') {
                continue;
            }

            operators.add(new Operator(i, ch));
        }

        List<List<String>> allNumbers = new ArrayList<>();
        for (int i = 0; i < operators.size(); i++) {
            Operator operator = operators.get(i);

            int lastIndex;
            if (i == operators.size() - 1) {
                lastIndex = maxLineLength - 1;
            } else {
                lastIndex = operators.get(i + 1).index() - 1;
            }

            int n = lastIndex - operator.index() + 1;
            List<String> numbers = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                numbers.add("");
            }

            for (String inputLine : inputLines) {
                String p = inputLine.substring(operator.index(), Math.min(lastIndex + 1, inputLine.length()));
                for (int j = 0; j < p.length(); j++) {
                    char ch = p.charAt(j);
                    if (ch == ' ') {
                        continue;
                    }

                    numbers.set(j, numbers.get(j) + ch);
                }
            }

            allNumbers.add(numbers);
        }

        long result = 0;
        for (int i = 0; i < operators.size(); i++) {
            Operator operator = operators.get(i);
            result += allNumbers.get(i).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .reduce((v1, v2) -> switch (operator.operator()) {
                        case '+' -> v1 + v2;
                        case '*' -> v1 * v2;
                        default -> throw new IllegalArgumentException("invalid operator: " + operator.operator());
                    })
                    .orElseThrow();
        }

        return result; // Your puzzle answer was 9770311947567.
    }

    private record Operator(int index, char operator) {
    }
}
