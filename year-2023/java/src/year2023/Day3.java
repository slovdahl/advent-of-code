package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static year2023.Common.readInputLinesForDay;
import static year2023.Common.result;
import static year2023.Common.startPart1;
import static year2023.Common.startPart2;
import static year2023.Day3.RowNumberWithGearIndex.from;

class Day3 {

    public static void main(String[] args) throws IOException {
        startPart1();
        startPart2();

        List<String> lines = readInputLinesForDay(3).toList();

        char[][] input = Common.paddedMatrix(lines, '.');

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

        part1(input, numberIndices);
        part2(input, numberIndices);
    }

    /**
     * The engineer explains that an engine part seems to be missing from the engine, but nobody
     * can figure out which one. If you can add up all the part numbers in the engine schematic,
     * it should be easy to work out which part is missing.
     * <p>
     * The engine schematic (your puzzle input) consists of a visual representation of the engine.
     * There are lots of numbers and symbols you don't really understand, but apparently any
     * number adjacent to a symbol, even diagonally, is a "part number" and should be included in
     * your sum. (Periods (.) do not count as a symbol.)
     * <p>
     * Here is an example engine schematic:
     * <p>
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
     * <p>
     * In this schematic, two numbers are not part numbers because they are not adjacent to a
     * symbol: 114 (top right) and 58 (middle right). Every other number is adjacent to a symbol
     * and so is a part number; their sum is 4361.
     * <p>
     * Of course, the actual engine schematic is much larger. What is the sum of all of the part
     * numbers in the engine schematic?
     * <p>
     * Your puzzle answer was 509115.
     */
    public static void part1(char[][] input, List<RowNumber> numberIndices) {
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

        result(1, result);
    }

    /**
     * The missing part wasn't the only issue - one of the gears in the engine is wrong. A gear is
     * any * symbol that is adjacent to exactly two part numbers. Its gear ratio is the result of
     * multiplying those two numbers together.
     * <p>
     * This time, you need to find the gear ratio of every gear and add them all up so that the
     * engineer can figure out which gear needs to be replaced.
     * <p>
     * Consider the same engine schematic again:
     * <p>
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
     * <p>
     * In this schematic, there are two gears. The first is in the top left; it has part numbers
     * 467 and 35, so its gear ratio is 16345. The second gear is in the lower right; its gear
     * ratio is 451490. (The * adjacent to 617 is not a gear because it is only adjacent to one
     * part number.) Adding up all of the gear ratios produces 467835.
     *
     * What is the sum of all of the gear ratios in your engine schematic?
     *
     * Your puzzle answer was 75220503.
     */
    public static void part2(char[][] input, List<RowNumber> numberIndices) {
        List<RowNumberWithGearIndex> rowNumberAdjacentToGear = new ArrayList<>();

        for (RowNumber rowNumber : numberIndices) {
            char[] rowBefore = input[rowNumber.row - 1];
            char[] rowAfter = input[rowNumber.row + 1];

            if (isGear(input[rowNumber.row][rowNumber.start - 1])) {
                rowNumberAdjacentToGear.add(from(rowNumber, rowNumber.row, rowNumber.start - 1));
            }

            if (isGear(input[rowNumber.row][rowNumber.end + 1])) {
                rowNumberAdjacentToGear.add(from(rowNumber, rowNumber.row, rowNumber.end + 1));
            }


            for (int i = rowNumber.start - 1; i < rowNumber.end + 2; i++) {
                if (isGear(rowBefore[i])) {
                    rowNumberAdjacentToGear.add(from(rowNumber, rowNumber.row - 1, i));
                }

                if (isGear(rowAfter[i])) {
                    rowNumberAdjacentToGear.add(from(rowNumber, rowNumber.row + 1, i));
                }
            }
        }

        Map<GearIndex, List<RowNumber>> numbersByGear = rowNumberAdjacentToGear.stream()
                .collect(groupingBy(n -> new GearIndex(n.gearX, n.gearY)))
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() == 2)
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                e -> e.getValue().stream()
                                        .map(n -> new RowNumber(n.row, n.start, n.end))
                                        .toList()
                        )
                );

        var result = numbersByGear.values().stream()
                .map(
                        gearNumbers -> {
                            List<Integer> numbers = gearNumbers.stream()
                                    .map(n -> {
                                        String combined = String.valueOf(input[n.row], n.start, n.end - n.start + 1);
                                        return Integer.parseInt(combined);
                                    })
                                    .toList();

                            return new GearNumbers(numbers.getFirst(), numbers.getLast());
                        }
                )
                .map(gearNumbers -> gearNumbers.n1 * gearNumbers.n2)
                .mapToInt(v -> v)
                .sum();

        result(2, result);
    }

    record RowNumber(int row, int start, int end) {
    }

    record RowNumberWithGearIndex(int row, int start, int end, int gearX, int gearY) {
        static RowNumberWithGearIndex from(RowNumber rowNumber, int gearX, int gearY) {
            return new RowNumberWithGearIndex(
                    rowNumber.row,
                    rowNumber.start,
                    rowNumber.end,
                    gearX,
                    gearY
            );
        }
    }

    record GearIndex(int x, int y) {
    }

    record GearNumbers(int n1, int n2) {
    }

    private static boolean isValidSymbol(char ch) {
        return ch != '.' && !Character.isDigit(ch) && !Character.isSpaceChar(ch);
    }

    private static boolean isGear(char ch) {
        return ch == '*';
    }
}
