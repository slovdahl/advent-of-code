package year2024;

import com.google.common.primitives.Ints;
import lib.Day;
import lib.Parse;

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
                    long comboOperand = comboOperand(operand, registerA, registerB, registerC);
                    out.add((int) (comboOperand % 8));
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

        /*
        A = 6816
        B = 0
        C = 0

        1. B = A % 8        => B = 0
        2. B = B ^ 3        => B = 3
        3. C = A / (2^B)    => C = 852
        4. B = B ^ 5        => B = 6
        5. A = A / (2^3)    => A = 852
        6. B = B ^ C        => B = 850
        7. out B % 8        => 2
        8. jmp 1

        1. B = A % 8        => B = 4
        2. B = B ^ 3        => B = 7
        3. C = A / (2^B)    => C = 6
        4. B = B ^ 5        => B = 2
        5. A = A / (2^3)    => A = 106
        6. B = B ^ C        => B = 4
        7. out B % 8        => 4
        8. jmp 1

        1. B = A % 8        => B = 2
        2. B = B ^ 3        => B = 1
        3. C = A / (2^B)    => C = 53
        4. B = B ^ 5        => B = 4
        5. A = A / (2^3)    => A = 13
        6. B = B ^ C        => B = 49
        7. out B % 8        => 1
        8. jmp 1

        1. B = A % 8        => B = 5
        2. B = B ^ 3        => B = 6
        3. C = A / (2^B)    => C = 0
        4. B = B ^ 5        => B = 3
        5. A = A / (2^3)    => A = 1
        6. B = B ^ C        => B = 3
        7. out B % 8        => 3
        8. jmp 1

        1. B = A % 8        => B = 1
        2. B = B ^ 3        => B = 2
        3. C = A / (2^B)    => C = 0
        4. B = B ^ 5        => B = 7
        5. A = A / (2^3)    => A = 0
        6. B = B ^ C        => B = 7
        7. out B % 8        => 7
        8. no jmp           => exit
         */

        // tested until 13180000000
        // tested until 14080000000
        // tested until 20000000000
        // tested until 35300000000
        // tested until 42400000000
        // tested until 84400000000
        // tested until 132200000000
        // tested until 137400000000
        // tested until 169800000000
        // tested until 283800000000
        // tested until 297000000000
        // tested until 393300000000

        // 100001613696621 output : [2, 4, 1, 3, 7, 5, 1, 5, 0, 3, 4, 1]
        // 100001613696703 output : [2, 4, 1, 3, 7, 5, 1, 5, 0, 3, 4, 1]

        // with working algorithm
        // until 91400000000

        // also from 8^(16-1) with (int) (((6 ^ (a / 8))) % 8) == 2 filter
        // until 35313700000000
        // until 35403000000032
        // until 35638000000032
        // until 35720000000032
        // until 35861000000032

        // with (int) (((6 ^ (a / 8))) % 8) == 2 && (int) (((6 ^ (a / 8 / 8))) % 8) == 4
        // until 35997000000679

        // from 58548994179072L (6816 * 8^11)
        // tested until 58609000000000
        // tested until 58612000000000
        // tested until 58619000000000
        // tested until 59611000000000

        long res = LongStream.iterate(58619000000000L, i -> i + 1)
                .parallel()
//                .filter(a -> {
//                    // TODO: not working as intended
//                    long aModulo8 = a % 8;
//                    long denominator = (long) Math.pow(2, aModulo8 ^ 3);
//                    if (denominator == 0) {
//                        return false;
//                    }
//
//                    return (((aModulo8 ^ 3) ^ 5) ^ ((a / denominator))) % 8 == 2;
//                })
                .map(i -> {
                    if (i % 1_000_000_000L == 0) {
                        System.out.println("A: " + i);
                    }

                    long registerA = i;

                    List<Integer> out = new ArrayList<>(program.size());
                    while (registerA != 0L) {
                        int instruction1 = (int) (registerA % 8);
                        int instruction2 = instruction1 ^ 3;
                        long instruction3 = registerA / (1L << instruction2);
                        int instruction4 = instruction2 ^ 5;
                        long instruction5 = registerA / 8;
                        long instruction6 = instruction4 ^ instruction3;

                        registerA = instruction5;
                        out.add((int) instruction6 % 8);

                        if (!program.subList(0, out.size()).equals(out)) {
                            // We can fail fast, this output will never match the wanted program
                            return -1;
                        }

                        if (out.size() == program.size() && out.equals(program)) {
                            System.out.println(i + " output : " + out);
                            return i;
                        } else if (out.size() >= program.size()) {
                            // We can fail fast, this output will never match the wanted program
                            System.out.println("Output longer than expected: " + out);
                            return -1;
                        } else if (out.size() > 10) {
                            System.out.println(i + " output : " + out);
                        }
                    }

                    return -1;
                })
                .filter(i -> i >= 0)
                .findFirst()
                .orElseThrow();

        if (true) {
            return res;
        }

        // from 1000000000000000
        // to 1000095500000000

        // with fixed division (truncate to int)
        // until 39200000000
        // 3978907179 output : [2, 4, 1, 3, 7, 5, 1, 5, 0, 3]
        return LongStream.iterate(3978907179L, i -> i + 1L)
                //.parallel()
                .limit(1)
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
                                        } else if (out.size() > 9) {
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

        // 35313700000000 too low
        // 58548994179072 too low
        // 502932030425617215258624 too high
    }

    private static long comboOperand(int operand, long registerA, long registerB, long registerC) {
        return switch (operand) {
            case 0, 1, 2, 3 -> operand;
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            default -> throw new IllegalStateException("Unexpected value: " + operand);
        };
    }

    private static long division(int operand, long registerA, long registerB, long registerC) {
        long comboOperand = comboOperand(operand, registerA, registerB, registerC);
        if (comboOperand >= 63) {
            return 0L;
        }

        return registerA / (1L << comboOperand);
    }
}
