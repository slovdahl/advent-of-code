package year2024;

import lib.Common;
import lib.Day;
import lib.Parse;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day7 extends Day {

    public static final char[] OPERATORS_PART_1 = {'+', '*'};
    public static final char[] OPERATORS_PART_2 = {'+', '*', '|'};

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
                    return new ResultAndNumbers(result, numbers, Common.generate(OPERATORS_PART_1, numbers.size() - 1));
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

    @Override
    protected Object part2(Stream<String> input) {
        return input
                .map(line -> line.split(":"))
                .parallel()
                .map(pair -> {
                    long result = Long.parseLong(pair[0]);
                    List<Long> numbers = Parse.longs(pair[1]);
                    return new ResultAndNumbers(result, numbers, Common.generate(OPERATORS_PART_2, numbers.size() - 1));
                })
                .filter(r -> {
                    for (String operator : r.operators()) {
                        long sum = r.numbers().getFirst();
                        for (int i = 1; i < r.numbers().size(); i++) {
                            sum = switch (operator.charAt(i - 1)) {
                                case '+' -> sum + r.numbers().get(i);
                                case '*' -> sum * r.numbers().get(i);
                                case '|' -> Long.parseLong(String.valueOf(sum) + r.numbers().get(i));
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
                .sum(); // Your puzzle answer was 70597497486371
    }

    private record ResultAndNumbers(long result, List<Long> numbers, List<String> operators) {
    }
}
