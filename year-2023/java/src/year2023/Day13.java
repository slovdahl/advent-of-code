package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day13 extends Day {

    @Override
    Integer part1(Stream<String> input) throws IOException {
        List<String> inputLines = input.toList();
        List<List<char[]>> patterns = new ArrayList<>();

        List<char[]> current = new ArrayList<>();
        for (String inputLine : inputLines) {
            if (inputLine.isEmpty()) {
                patterns.add(current);
                current = new ArrayList<>();
                continue;
            }

            current.add(inputLine.toCharArray());
        }

        patterns.add(current);

        int rowSummary = 0;
        int columnSummary = 0;

        for (int i = 0; i < patterns.size(); i++) {
            List<char[]> pattern = patterns.get(i);

            // rows
            boolean hasFullRowMirror = false;
            int innerMostRowMirror1 = -1;
            int innerMostRowMirror2 = -1;

            {
                int row1Index = 0;
                int row2Index = 1;

                boolean foundInitialMirror = false;

                while (true) {
                    char[] row1 = pattern.get(row1Index);
                    char[] row2 = pattern.get(row2Index);

                    boolean mirror = areRowMirrors(row1, row2);

                    if (mirror) {
                        System.out.println(i + ": FOUND INITIAL ROW MIRROR AT " + row1Index + " AND " + row2Index);

                        if (!foundInitialMirror) {
                            innerMostRowMirror1 = row1Index;
                            innerMostRowMirror2 = row2Index;
                            foundInitialMirror = true;
                        }

                        if (row1Index == 0 || row2Index + 1 == pattern.size()) {
                            hasFullRowMirror = true;
                            System.out.println(i + ": FULL ROW MIRROR STARTING AT " + innerMostRowMirror1 + " AND " + innerMostRowMirror2);
                            break;
                        }

                        row1Index--;
                        row2Index++;
                    } else if (!mirror && foundInitialMirror) {
                        if (innerMostRowMirror2 + 1 < pattern.size()) {
                            row1Index = innerMostRowMirror1 + 1;
                            row2Index = innerMostRowMirror2 + 1;
                        }
                        innerMostRowMirror1 = innerMostRowMirror2 = -1;
                        foundInitialMirror = false;
                    } else if (row2Index + 1 < pattern.size()) {
                        row1Index++;
                        row2Index++;
                    } else {
                        break;
                    }
                }
            }

            // columns
            boolean hasFullColumnMirror = false;
            int innerMostColumnMirror1 = -1;
            int innerMostColumnMirror2 = -1;

            if (!hasFullRowMirror) {
                int column1Index = 0;
                int column2Index = 1;
                boolean foundInitialMirror = false;

                while (true) {
                    boolean mirror = areColumnMirrors(pattern, column1Index, column2Index);

                    if (mirror) {
                        System.out.println(i + ": FOUND INITIAL COLUMN MIRROR AT " + column1Index + " AND " + column2Index);

                        if (!foundInitialMirror) {
                            innerMostColumnMirror1 = column1Index;
                            innerMostColumnMirror2 = column2Index;
                            foundInitialMirror = true;
                        }

                        if (column1Index == 0 || column2Index + 1 == pattern.getFirst().length) {
                            hasFullColumnMirror = true;
                            System.out.println(i + ": FULL COLUMN MIRROR STARTING AT " + innerMostColumnMirror1 + " AND " + innerMostColumnMirror2);
                            break;
                        }

                        column1Index--;
                        column2Index++;
                    } else if (!mirror && foundInitialMirror) {
                        if (innerMostColumnMirror2 + 1 < pattern.getFirst().length) {
                            column1Index = innerMostColumnMirror1 + 1;
                            column2Index = innerMostColumnMirror2 + 1;
                        }
                        innerMostColumnMirror1 = innerMostColumnMirror2 = -1;
                        foundInitialMirror = false;
                    } else if (column2Index + 1 < pattern.getFirst().length) {
                        column1Index++;
                        column2Index++;
                    } else {
                        break;
                    }
                }
            }

            if (hasFullRowMirror) {
                rowSummary += (innerMostRowMirror1 + 1) * 100;
            } else if (hasFullColumnMirror) {
                columnSummary += innerMostColumnMirror1 + 1;
            } else {
                throw new IllegalStateException("neither row nor column mirror found");
            }
        }

        return rowSummary + columnSummary; // Your puzzle answer was 34202
    }

    private static boolean areRowMirrors(char[] row1, char[] row2) {
        for (int column = 0; column < row1.length; column++) {
            if (row1[column] != row2[column]) {
                return false;
            }
        }

        return true;
    }

    private static boolean areColumnMirrors(List<char[]> pattern, int column1, int column2) {
        for (char[] currentRow : pattern) {
            if (currentRow[column1] != currentRow[column2]) {
                return false;
            }
        }

        return true;
    }
}
