package year2025;

import lib.Day;
import lib.Parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day6 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<String> inputLines = input.collect(Collectors.toList());
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
}
