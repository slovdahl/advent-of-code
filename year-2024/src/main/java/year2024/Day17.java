package year2024;

import lib.Day;
import lib.Parse;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
public class Day17 extends Day {

    private Map<String, Integer> registerSection;
    private int initialRegisterA;
    private int initialRegisterB;
    private int initialRegisterC;
    private List<Integer> program;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);
        List<String> registerSection = sections.getFirst();

        for (String registerString : registerSection) {
            Matcher matcher = Pattern.compile("^Register ([A-Z]): ([0-9]+)").matcher(registerString);
            checkState(matcher.find());

            if (matcher.group(1).equals("A")) {
                initialRegisterA = Integer.parseInt(matcher.group(2));
            } else if (matcher.group(1).equals("B")) {
                initialRegisterB = Integer.parseInt(matcher.group(2));
            } else if (matcher.group(1).equals("C")) {
                initialRegisterC = Integer.parseInt(matcher.group(2));
            } else {
                throw new IllegalArgumentException("Unknown register '" + registerString + "'");
            }
        }

        String programSection = sections.getLast().getFirst();
        program = Parse.commaSeparatedInts(programSection.substring("Program: ".length()));
    }

    @Override
    protected Object part1(Stream<String> input) {
        long registerA = initialRegisterA;
        long registerB = initialRegisterB;
        long registerC = initialRegisterC;
        int instructionPointer = 0;
        List<Integer> out = new ArrayList<>();

        while (true) {
            if (instructionPointer >= program.size()) {
                break;
            }
            Integer instruction = program.get(instructionPointer);
            Integer operand = program.get(instructionPointer + 1);

            switch (instruction) {
                // adv
                case 0 -> {
                    registerA = registerA / (int) Math.pow(2, comboOperand(operand, registerA, registerB, registerC));
                    instructionPointer += 2;
                }

                // bxl
                case 1 -> {
                    registerB = registerB ^ operand;
                    instructionPointer += 2;
                }

                // bst
                case 2 -> {
                    registerB = comboOperand(operand, registerA, registerB, registerC) % 8L;
                    instructionPointer += 2;
                }

                // jnz
                case 3 -> {
                    if (registerA != 0) {
                        instructionPointer = operand;
                    } else {
                        instructionPointer += 2;
                    }
                }

                // bxc
                case 4 -> {
                    registerB = registerB ^ registerC;
                    instructionPointer += 2;
                }

                // out
                case 5 -> {
                    // TODO: fix
                    out.add((int) (comboOperand(operand, registerA, registerB, registerC) % 8));
                    instructionPointer += 2;
                }

                // bdv
                case 6 -> {
                    registerB = registerA / (int) Math.pow(2, comboOperand(operand, registerA, registerB, registerC));
                    instructionPointer += 2;
                }

                // cdv
                case 7 -> {
                    registerC = registerA / (int) Math.pow(2, comboOperand(operand, registerA, registerB, registerC));
                    instructionPointer += 2;
                }

                default -> throw new IllegalStateException("Unexpected value: " + instruction);
            }
        }

        return out.stream()
                .map(String::valueOf)
                .collect(joining(",")); // Your puzzle answer was 6,7,5,2,1,3,5,1,7
    }

    @Override
    protected Object part2(Stream<String> input) {
        /*
        0: 2,4
        1: 1,3
        2: 7,5
        3: 1,5
        4: 0,3
        5: 4,1
        6: 5,5
        7: 3,0
         */

        /*
        0 output : [6]
        1 output : [6]
        2 output : [6]
        3 output : [0]
        4 output : [2]
        5 output : [3]
        6 output : [0]
        7 output : [1]

        100 output : [2]
        101 output : [3]
        102 output : [0]
        103 output : [1]
        104 output : [6]
        105 output : [6]
        106 output : [6]
        107 output : [0]
         */

        /*
        4 output : [2]
        2751 output : [2, 4, 1, 3]
        6816 output : [2, 4, 1, 3, 7]
        6847 output : [2, 4, 1, 3, 7]
        105995885 output : [2, 4, 1, 3, 7, 5, 1, 5, 0]
        105995967 output : [2, 4, 1, 3, 7, 5, 1, 5, 0]
        106126957 output : [2, 4, 1, 3, 7, 5, 1, 5, 0]
        106127039 output : [2, 4, 1, 3, 7, 5, 1, 5, 0]
         */

        // tested until 13180000000
        // tested until 14080000000
        // tested until 20000000000
        // tested until 35300000000
        // tested until 42400000000
        // tested until 84400000000
        // tested until 132200000000
        // tested until 137400000000
        return LongStream.iterate(137_400_000_000L, i -> i + 1)
                .parallel()
                .map(i -> {
                            if (i % 100_000_000L == 0) {
                                System.out.println("A: " + i);
                            }

                            long registerA = i;
                            long registerB = initialRegisterB;
                            long registerC = initialRegisterC;
                            int instructionPointer = 0;
                            List<Integer> out = new ArrayList<>(program.size());

                            while (true) {
                                if (instructionPointer >= program.size()) {
                                    break;
                                }
                                Integer instruction = program.get(instructionPointer);
                                Integer operand = program.get(instructionPointer + 1);

                                switch (instruction) {
                                    // adv
                                    case 0 -> {
                                        registerA = division(operand, registerA, registerB, registerC);
                                        instructionPointer += 2;
                                    }

                                    // bxl
                                    case 1 -> {
                                        registerB = registerB ^ operand;
                                        instructionPointer += 2;
                                    }

                                    // bst
                                    case 2 -> {
                                        registerB = comboOperand(operand, registerA, registerB, registerC) % 8;
                                        instructionPointer += 2;
                                    }

                                    // jnz
                                    case 3 -> {
                                        if (registerA != 0) {
                                            instructionPointer = operand;
                                        } else {
                                            instructionPointer += 2;
                                        }
                                    }

                                    // bxc
                                    case 4 -> {
                                        registerB = registerB ^ registerC;
                                        instructionPointer += 2;
                                    }

                                    // out
                                    case 5 -> {
                                        long comboOperand = comboOperand(operand, registerA, registerB, registerC);
                                        out.add((int) (comboOperand % 8));

                                        if (!program.subList(0, out.size()).equals(out)) {
                                            // We can fail fast, this output will never match the wanted program
                                            return -1;
                                        }

                                        if (out.size() == program.size() && out.equals(program)) {
                                            System.out.println(i + " output : " + out);
                                            return i;
                                        } else if (out.size() >= program.size()) {
                                            // We can fail fast, this output will never match the wanted program
                                            return -1;
                                        } else if (out.size() > 12) {
                                            System.out.println(i + " output : " + out);
                                        }

                                        instructionPointer += 2;
                                    }

                                    // bdv
                                    case 6 -> {
                                        registerB = division(operand, registerA, registerB, registerC);
                                        instructionPointer += 2;
                                    }

                                    // cdv
                                    case 7 -> {
                                        registerC = division(operand, registerA, registerB, registerC);
                                        instructionPointer += 2;
                                    }

                                    default -> throw new IllegalStateException("Unexpected value: " + instruction);
                                }
                            }

                            return -1;
                        }
                )
                .filter(i -> i >= 0)
                .findFirst()
                .orElseThrow();
    }

    private static long comboOperand(int operand, long registerA, long registerB, long registerC) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> BigInteger.valueOf(registerA).longValueExact();
            case 5 -> BigInteger.valueOf(registerB).longValueExact();
            case 6 -> BigInteger.valueOf(registerC).longValueExact();
            default -> throw new IllegalStateException("Unexpected value: " + operand);
        };
    }

    private static long division(int operand, long registerA, long registerB, long registerC) {
        long comboOperand = comboOperand(operand, registerA, registerB, registerC);
        if (comboOperand >= 63) {
            return 0L;
        }

        return registerA / (long) Math.pow(2, comboOperand);
    }
}
