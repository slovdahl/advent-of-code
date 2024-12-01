package year2023;

import lib.Day;
import lib.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static year2023.Day3.RowNumberWithGearIndex.from;

@SuppressWarnings("unused")
public class Day3 extends Day {

    private char[][] input;
    private List<RowNumber> numberIndices;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> rawInput) {
        input = Matrix.paddedMatrix(rawInput.toList(), '.');
        numberIndices = new ArrayList<>();

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
    }

    @Override
    protected Object part1(Stream<String> ignored) throws Exception {
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

        return validRowNumbers.stream()
                .map(n -> {
                    String numbers = String.valueOf(input[n.row], n.start, n.end - n.start + 1);
                    return Integer.parseInt(numbers);
                })
                .mapToInt(v -> v)
                .sum(); // Your puzzle answer was 509115
    }

    @Override
    protected Object part2(Stream<String> ignored) throws Exception {
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

        return numbersByGear.values().stream()
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
                .sum(); // Your puzzle answer was 75220503
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
