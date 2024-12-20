package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
public class Day17 extends Day {

    private int registerA;
    private int registerB;
    private int registerC;
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
                registerA = Integer.parseInt(matcher.group(2));
            } else if (matcher.group(1).equals("B")) {
                registerB = Integer.parseInt(matcher.group(2));
            } else if (matcher.group(1).equals("C")) {
                registerC = Integer.parseInt(matcher.group(2));
            } else {
                throw new IllegalArgumentException("Unknown register '" + registerString + "'");
            }
        }

        String programSection = sections.getLast().getFirst();
        program = Parse.commaSeparatedInts(programSection.substring("Program: ".length()));
    }

    @Override
    protected Object part1(Stream<String> input) {
        int instructionPointer = 0;
        List<Integer> out = new ArrayList<>();

        while (true) {
            Integer instruction;
            try {
                instruction = program.get(instructionPointer);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            switch (instruction) {
                // adv
                case 0 -> {
                    Integer operand = program.get(instructionPointer + 1);
                    registerA = registerA / (int) Math.pow(2, comboOperand(operand));
                    instructionPointer += 2;
                }

                // bxl
                case 1 -> {
                    Integer operand = program.get(instructionPointer + 1);
                    registerB = registerB ^ operand;
                    instructionPointer += 2;
                }

                // bst
                case 2 -> {
                    Integer operand = program.get(instructionPointer + 1);
                    registerB = comboOperand(operand) % 8;
                    instructionPointer += 2;
                }

                // jnz
                case 3 -> {
                    if (registerA != 0) {
                        instructionPointer = program.get(instructionPointer + 1);
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
                    Integer operand = program.get(instructionPointer + 1);
                    out.add(comboOperand(operand) % 8);
                    instructionPointer += 2;
                }

                // bdv
                case 6 -> {
                    Integer operand = program.get(instructionPointer + 1);
                    registerB = registerA / (int) Math.pow(2, comboOperand(operand));
                    instructionPointer += 2;
                }

                // cdv
                case 7 -> {
                    Integer operand = program.get(instructionPointer + 1);
                    registerC = registerA / (int) Math.pow(2, comboOperand(operand));
                    instructionPointer += 2;
                }

                default -> throw new IllegalStateException("Unexpected value: " + instruction);
            }
        }

        return out.stream()
                .map(String::valueOf)
                .collect(joining(",")); // Your puzzle answer was 6,7,5,2,1,3,5,1,7
    }

    private int comboOperand(int operand) {
        return switch (operand) {
            case 0 -> 0;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> registerA;
            case 5 -> registerB;
            case 6 -> registerC;
            default -> throw new IllegalStateException("Unexpected value: " + operand);
        };
    }
}
