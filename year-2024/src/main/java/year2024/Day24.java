package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lib.Day;
import lib.Parse;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
public class Day24 extends Day {

    private static final Pattern WIRE_VALUE_PATTERN = Pattern.compile("^([xy][0-9][0-9]): ([01])");
    private static final Pattern GATE_PATTERN = Pattern.compile("^([a-z0-9]{3}) (AND|OR|XOR) ([a-z0-9]{3}) -> ([a-z0-9]{3})");

    private Map<String, Gate> outputToGate;
    private Map<String, Integer> wireValues;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);

        outputToGate = new HashMap<>();
        wireValues = new HashMap<>();

        for (String gate : sections.getLast()) {
            Matcher matcher = GATE_PATTERN.matcher(gate);
            checkState(matcher.find());

            Gate g = new Gate(
                    matcher.group(1),
                    Op.valueOf(matcher.group(2)),
                    matcher.group(3),
                    matcher.group(4)
            );

            outputToGate.put(g.output, g);
        }

        for (String initialValue : sections.getFirst()) {
            Matcher matcher = WIRE_VALUE_PATTERN.matcher(initialValue);
            checkState(matcher.find());

            wireValues.put(
                    matcher.group(1),
                    Integer.parseInt(matcher.group(2))
            );
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Integer> zOutputs = getFinalOutputs(wireValues, outputToGate);

        return littleEndianBitsToLong(zOutputs); // Your puzzle answer was 69201640933606
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        long expectedZ = initialValue("x") + initialValue("y");

        List<Integer> differingBits = getBitsDifferingFrom(expectedZ);

        SetMultimap<Integer, Gate> candidateGates = HashMultimap.create();
        for (Integer differingBit : differingBits) {
            Gate gate = outputToGate.get("z%02d".formatted(differingBit));

            findConnectionsRecursively(differingBit, gate, candidateGates);
        }

        return 0;
    }

    private static List<Integer> getFinalOutputs(Map<String, Integer> wireValues, Map<String, Gate> outputToGate) {
        return IntStream.range(0, 64)
                .mapToObj(i -> outputToGate.get("z%02d".formatted(i)))
                .filter(Objects::nonNull)
                .map((Gate gate) -> invokeRecursively(gate, wireValues, outputToGate))
                .toList();
    }

    private long initialValue(String inputVariable) {
        List<Integer> outputs = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Integer value = wireValues.get(inputVariable + "%02d".formatted(i));
            if (value == null) {
                break;
            }

            outputs.add(value);
        }

        return littleEndianBitsToLong(outputs);
    }

    private static int invokeRecursively(Gate gate, Map<String, Integer> wireValues, Map<String, Gate> outputToGate) {
        Integer input1 = wireValues.get(gate.input1);
        Integer input2 = wireValues.get(gate.input2);
        if (input1 != null && input2 != null) {
            int result = gate.op.invoke(input1, input2);
            wireValues.put(gate.output, result);
            return result;
        }

        if (input1 == null) {
            input1 = invokeRecursively(outputToGate.get(gate.input1), wireValues, outputToGate);
        }
        if (input2 == null) {
            input2 = invokeRecursively(outputToGate.get(gate.input2), wireValues, outputToGate);
        }

        int result = gate.op.invoke(input1, input2);
        wireValues.put(gate.output, result);
        return result;
    }

    private List<Integer> getBitsDifferingFrom(long expectedZ) {
        List<Integer> reversedZ = getFinalOutputs(wireValues, outputToGate).reversed();
        char[] expectedZBinary = Long.toBinaryString(expectedZ).toCharArray();
        List<Integer> differingBits = new ArrayList<>();
        for (int i = 0; i < reversedZ.size(); i++) {
            if (reversedZ.get(i) != Character.digit(expectedZBinary[i], 10)) {
                differingBits.add(i);
            }
        }
        return differingBits;
    }

    private void findConnectionsRecursively(int bit, @Nullable Gate gate, SetMultimap<Integer, Gate> gates) {
        if (gate == null) {
            return;
        }

        gates.put(bit, gate);

        findConnectionsRecursively(bit, outputToGate.get(gate.input1), gates);
        findConnectionsRecursively(bit, outputToGate.get(gate.input2), gates);
    }

    private static long littleEndianBitsToLong(List<Integer> bits) {
        return Long.parseLong(
                bits
                        .reversed()
                        .stream()
                        .map(String::valueOf)
                        .collect(joining("")),
                2
        );
    }

    private record Gate(String input1, Op op, String input2, String output) {
    }

    private enum Op {
        AND {
            @Override
            int invoke(int input1, int input2) {
                return input1 == 1 && input2 == 1 ? 1 : 0;
            }
        },

        OR {
            @Override
            int invoke(int input1, int input2) {
                return input1 == 1 || input2 == 1 ? 1 : 0;
            }
        },

        XOR {
            @Override
            int invoke(int input1, int input2) {
                return input1 != input2 ? 1 : 0;
            }
        };

        abstract int invoke(int input1, int input2);
    }
}
