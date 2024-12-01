package year2023;

import lib.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day13 extends Day {

    private List<List<char[]>> patterns;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<String> inputLines = input.toList();
        patterns = new ArrayList<>();

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
    }

    @Override
    protected Integer part1(Stream<String> input) throws IOException {
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
                        if (!foundInitialMirror) {
                            innerMostRowMirror1 = row1Index;
                            innerMostRowMirror2 = row2Index;
                            foundInitialMirror = true;
                        }

                        if (row1Index == 0 || row2Index + 1 == pattern.size()) {
                            hasFullRowMirror = true;
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
                        innerMostRowMirror1 = innerMostRowMirror2 = -1;
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
                        if (!foundInitialMirror) {
                            innerMostColumnMirror1 = column1Index;
                            innerMostColumnMirror2 = column2Index;
                            foundInitialMirror = true;
                        }

                        if (column1Index == 0 || column2Index + 1 == pattern.getFirst().length) {
                            hasFullColumnMirror = true;
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
                        innerMostColumnMirror1 = innerMostColumnMirror2 = -1;
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

    @Override
    protected Integer part2(Stream<String> input) throws Exception {
        Map<Integer, Integer> lastHalfRowPartOfMirror = new LinkedHashMap<>();
        Map<Integer, Integer> lastHalfColumnPartOfMirror = new LinkedHashMap<>();

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
                        if (!foundInitialMirror) {
                            innerMostRowMirror1 = row1Index;
                            innerMostRowMirror2 = row2Index;
                            foundInitialMirror = true;
                        }

                        if (row1Index == 0 || row2Index + 1 == pattern.size()) {
                            hasFullRowMirror = true;
                            lastHalfRowPartOfMirror.put(i, innerMostRowMirror1);
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
                        innerMostRowMirror1 = innerMostRowMirror2 = -1;
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
                        if (!foundInitialMirror) {
                            innerMostColumnMirror1 = column1Index;
                            innerMostColumnMirror2 = column2Index;
                            foundInitialMirror = true;
                        }

                        if (column1Index == 0 || column2Index + 1 == pattern.getFirst().length) {
                            hasFullColumnMirror = true;
                            lastHalfColumnPartOfMirror.put(i, innerMostColumnMirror1);
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
                        innerMostColumnMirror1 = innerMostColumnMirror2 = -1;
                        break;
                    }
                }
            }

            if (!hasFullRowMirror && !hasFullColumnMirror) {
                throw new IllegalStateException("neither row nor column mirror found");
            }
        }

        int rowSummary = 0;
        int columnSummary = 0;

        for (int i = 0; i < patterns.size(); i++) {
            List<char[]> pattern = patterns.get(i);

            boolean hasFullRowMirror = false;
            int innerMostRowMirror1 = -1;
            int innerMostRowMirror2 = -1;

            boolean hasFullColumnMirror = false;
            int innerMostColumnMirror1 = -1;
            int innerMostColumnMirror2 = -1;

            outer:
            for (int row = 0; row < pattern.size(); row++) {
                for (int column = 0; column < pattern.get(row).length; column++) {
                    char previous = pattern.get(row)[column];
                    pattern.get(row)[column] = previous == '.' ? '#' : '.';

                    innerMostRowMirror1 = innerMostRowMirror2 = -1;
                    innerMostColumnMirror1 = innerMostColumnMirror2 = -1;

                    // rows
                    {
                        int row1Index = 0;
                        int row2Index = 1;

                        boolean foundInitialMirror = false;

                        while (true) {
                            if (lastHalfRowPartOfMirror.getOrDefault(i, -1) == row1Index) {
                                if (row2Index + 1 < pattern.size()) {
                                    row1Index++;
                                    row2Index++;
                                } else {
                                    break;
                                }
                            }

                            char[] row1 = pattern.get(row1Index);
                            char[] row2 = pattern.get(row2Index);

                            boolean mirror = areRowMirrors(row1, row2);

                            if (mirror) {
                                if (!foundInitialMirror) {
                                    innerMostRowMirror1 = row1Index;
                                    innerMostRowMirror2 = row2Index;
                                    foundInitialMirror = true;
                                }

                                if (row1Index == 0 || row2Index + 1 == pattern.size()) {
                                    hasFullRowMirror = true;
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
                    if (!hasFullRowMirror) {
                        int column1Index = 0;
                        int column2Index = 1;
                        boolean foundInitialMirror = false;

                        while (true) {
                            if (lastHalfColumnPartOfMirror.getOrDefault(i, -1) == column1Index) {
                                if (column2Index + 1 < pattern.getFirst().length) {
                                    column1Index++;
                                    column2Index++;
                                } else {
                                    break;
                                }
                            }

                            boolean mirror = areColumnMirrors(pattern, column1Index, column2Index);

                            if (mirror) {
                                if (!foundInitialMirror) {
                                    innerMostColumnMirror1 = column1Index;
                                    innerMostColumnMirror2 = column2Index;
                                    foundInitialMirror = true;
                                }

                                if (column1Index == 0 || column2Index + 1 == pattern.getFirst().length) {
                                    hasFullColumnMirror = true;
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

                    if (hasFullRowMirror || hasFullColumnMirror) {
                        break outer;
                    } else {
                        pattern.get(row)[column] = previous;
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

        return rowSummary + columnSummary; // Your puzzle answer was 34230
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
