package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static eu.lovdahl.advent.of.code.Common.readInputLinesForDay;

/**
 *
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
class Day3 {

    public static void main(String[] args) throws IOException {
        List<String> lines = readInputLinesForDay(3).toList();

        int numberOfRows = lines.size() + 2;
        int numberOfColumns = lines.getFirst().length() + 2;

        char[][] input = new char[numberOfRows][numberOfColumns];
        Arrays.fill(input[0], '.');
        Arrays.fill(input[numberOfRows - 1], '.');

        for (char[] line : input) {
            line[0] = '.';
            line[numberOfColumns - 1] = '.';
        }

        for (int i = 0; i < lines.size(); i++) {
            char[] sourceArray = lines.get(i).toCharArray();
            System.arraycopy(
                    sourceArray,
                    0,
                    input[i + 1],
                    1,
                    sourceArray.length
            );
        }

        /*
        for (char[] line : input) {
            for (char ch : line) {
                System.out.print(ch);
            }
            System.out.println();
        }
        */

        part1(input);
    }

    /**
     * The engineer explains that an engine part seems to be missing from the engine, but nobody
     * can figure out which one. If you can add up all the part numbers in the engine schematic,
     * it should be easy to work out which part is missing.
     *
     * The engine schematic (your puzzle input) consists of a visual representation of the engine.
     * There are lots of numbers and symbols you don't really understand, but apparently any
     * number adjacent to a symbol, even diagonally, is a "part number" and should be included in
     * your sum. (Periods (.) do not count as a symbol.)
     *
     * Here is an example engine schematic:
     *
     * 467..114..
     * ...*......
     * ..35..633.
     * ......#...
     * 617*......
     * .....+.58.
     * ..592.....
     * ......755.
     * ...$.*....
     * .664.598..
     *
     * In this schematic, two numbers are not part numbers because they are not adjacent to a
     * symbol: 114 (top right) and 58 (middle right). Every other number is adjacent to a symbol
     * and so is a part number; their sum is 4361.
     *
     * Of course, the actual engine schematic is much larger. What is the sum of all of the part
     * numbers in the engine schematic?
     *
     * Your puzzle answer was 509115.
     */
    public static void part1(char[][] input) {
        List<RowNumber> numberIndices = new ArrayList<>();

        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[i].length; j++) {
                char ch = input[i][j];
                if (Character.isDigit(ch)) {
                    int startIndex = j;
                    int endIndex = -1;

                    while (j < input[i].length) {
                        j++;
                        char nextCh = input[i][j];
                        if (Character.isDigit(nextCh)) {
                            endIndex = j;
                        } else {
                            break;
                        }
                    }

                    if (endIndex == -1) {
                        endIndex = startIndex;
                    }

                    numberIndices.add(new RowNumber(i, startIndex, endIndex));
                }
            }
        }

        List<RowNumber> validRowNumbers = new ArrayList<>();

        for (RowNumber rowNumber : numberIndices) {
            char[] rowBefore = input[rowNumber.row - 1];
            char[] rowAfter = input[rowNumber.row + 1];

            if (isValidSymbol(input[rowNumber.row][rowNumber.start - 1]) ||
                    isValidSymbol(input[rowNumber.row][rowNumber.end + 1])) {

                validRowNumbers.add(rowNumber);
                continue;
            }

            for (int i = rowNumber.start - 1; i < rowNumber.end + 2; i++) {
                if (isValidSymbol(rowBefore[i]) || isValidSymbol(rowAfter[i])) {
                    validRowNumbers.add(rowNumber);
                    break;
                }
            }
        }

        var result = validRowNumbers.stream()
                .map(n -> {
                    String numbers = String.valueOf(input[n.row], n.start, n.end - n.start + 1);
                    return Integer.parseInt(numbers);
                })
                .mapToInt(v -> v)
                .sum();

        System.out.println("Part 1: " + result);
    }

    record RowNumber(int row, int start, int end) {
    }

    private static boolean isValidSymbol(char ch) {
        return ch != '.' && !Character.isDigit(ch) && !Character.isSpaceChar(ch);
    }
}
