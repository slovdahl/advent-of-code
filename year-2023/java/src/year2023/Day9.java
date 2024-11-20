package year2023;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day9 extends Day {

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        return input
                .map(Parse::ints)
                .map(series -> {
                    List<List<Integer>> allLines = new ArrayList<>();
                    allLines.add(new ArrayList<>(series));

                    List<Integer> toCheck = series;
                    while (true) {
                        boolean allZeroes = true;
                        List<Integer> diffs = new ArrayList<>();

                        for (int i = 1; i < toCheck.size(); i++) {
                            int diff = toCheck.get(i) - toCheck.get(i - 1);
                            diffs.add(diff);

                            if (diff != 0) {
                                allZeroes = false;
                            }
                        }

                        allLines.add(diffs);

                        if (allZeroes) {
                            break;
                        } else {
                            toCheck = diffs;
                        }
                    }

                    List<List<Integer>> diffsReversed = allLines.reversed();
                    for (int i = 1; i < diffsReversed.size(); i++) {
                        int diff = diffsReversed.get(i - 1).getLast();
                        int last = diffsReversed.get(i).getLast();

                        diffsReversed.get(i).add(last + diff);
                    }

                    return allLines.getFirst().getLast();
                })
                .mapToInt(v -> v)
                .sum(); // Your puzzle answer was 1955513104
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return input
                .map(Parse::ints)
                .map(series -> {
                    List<List<Integer>> allLines = new ArrayList<>();
                    allLines.add(new ArrayList<>(series));

                    List<Integer> toCheck = series;
                    while (true) {
                        boolean allZeroes = true;
                        List<Integer> diffs = new ArrayList<>();

                        for (int i = 1; i < toCheck.size(); i++) {
                            int diff = toCheck.get(i) - toCheck.get(i - 1);
                            diffs.add(diff);

                            if (diff != 0) {
                                allZeroes = false;
                            }
                        }

                        allLines.add(diffs);

                        if (allZeroes) {
                            break;
                        } else {
                            toCheck = diffs;
                        }
                    }

                    List<List<Integer>> diffsReversed = allLines.reversed();
                    for (int i = 1; i < diffsReversed.size(); i++) {
                        int diff = diffsReversed.get(i - 1).getFirst();
                        int first = diffsReversed.get(i).getFirst();

                        diffsReversed.get(i).addFirst(first - diff);
                    }

                    return allLines.getFirst().getFirst();
                })
                .mapToInt(v -> v)
                .sum(); // Your puzzle answer was 1131
    }
}
