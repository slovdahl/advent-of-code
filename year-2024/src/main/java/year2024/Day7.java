package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day7 extends Day {

    public static final char[] OPERATORS = {'+', '*'};

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        return input
                .map(line -> line.split(":"))
                .parallel()
                .map(pair -> {
                    long result = Long.parseLong(pair[0]);
                    List<Long> numbers = Parse.longs(pair[1]);
                    return new ResultAndNumbers(result, numbers, generate(OPERATORS, numbers.size() - 1));
                })
                .filter(r -> {
                    for (String operator : r.operators()) {
                        long sum = r.numbers().getFirst();
                        for (int i = 1; i < r.numbers().size(); i++) {
                            sum = switch (operator.charAt(i - 1)) {
                                case '+' -> sum + r.numbers().get(i);
                                case '*' -> sum * r.numbers().get(i);
                                default -> throw new IllegalStateException("Unexpected value: " + operator);
                            };
                        }

                        if (sum == r.result()) {
                            return true;
                        }
                    }

                    return false;
                })
                .mapToLong(ResultAndNumbers::result)
                .sum(); // Your puzzle answer was 465126289353
    }

    public static List<String> generate(char[] chars, int length) {
        List<String> result = new ArrayList<>((int) Math.pow(chars.length, length));
        generate(chars, result, "", length);
        return result;
    }

    private static void generate(char[] chars, List<String> values, String current, int length) {
        if (length == 0) {
            values.add(current);
            return;
        }

        for (char c : chars) {
            String newPrefix = current + c;
            generate(chars, values, newPrefix, length - 1);
        }
    }

    private record ResultAndNumbers(long result, List<Long> numbers, List<String> operators) {
    }
}
