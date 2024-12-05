package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day5 extends Day {

    private List<Rule> rules;
    private SetMultimap<Integer, Integer> rulePerNumber;

    private List<List<Integer>> invalidUpdates;
    private List<List<Integer>> validUpdates;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        List<String> fullInput = input.toList();

        rules = fullInput.stream()
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split("\\|"))
                .map(pair -> new Rule(Integer.parseInt(pair[0]), Integer.parseInt(pair[1])))
                .toList();

        rulePerNumber = HashMultimap.create();
        for (Rule rule : rules) {
            rulePerNumber.put(rule.before(), rule.after());
        }

        List<List<Integer>> updateLines = fullInput.stream()
                .dropWhile(line -> !line.isEmpty())
                .filter(line -> !line.isEmpty())
                .map(Parse::commaSeparatedInts)
                .collect(Collectors.toList());

        List<List<Integer>> invalidUpdates = new ArrayList<>();

        Iterator<List<Integer>> iterator = updateLines.iterator();
        while (iterator.hasNext()) {
            List<Integer> updateLine = iterator.next();
            Set<Integer> seen = new HashSet<>();

            outer:
            for (Integer n : updateLine) {
                Set<Integer> shouldComeAfter = rulePerNumber.get(n);

                for (Integer s : seen) {
                    if (shouldComeAfter.contains(s)) {
                        invalidUpdates.add(updateLine);
                        iterator.remove();
                        break outer;
                    }
                }

                seen.add(n);
            }
        }

        this.invalidUpdates = List.copyOf(invalidUpdates);
        validUpdates = List.copyOf(updateLines);
    }

    @Override
    protected Object part1(Stream<String> input) {
        int sum = 0;
        for (List<Integer> updateLine : validUpdates) {
            int middleIndex = updateLine.size() / 2;
            sum += updateLine.get(middleIndex);
        }

        return sum; // Your puzzle answer was 4766
    }

    @Override
    protected Object part2(Stream<String> input) {
        int sum = 0;
        for (List<Integer> invalidUpdate : invalidUpdates) {
            List<Integer> updateLine = new ArrayList<>(invalidUpdate);
            Set<Integer> seen = new HashSet<>();

            outer:
            while (true) {
                for (int i = 0; i < updateLine.size(); i++) {
                    Integer n = updateLine.get(i);
                    Set<Integer> shouldComeAfter = rulePerNumber.get(n);

                    for (Integer s : seen) {
                        if (shouldComeAfter.contains(s)) {
                            Integer previous = updateLine.get(i - 1);
                            updateLine.set(i - 1, n);
                            updateLine.set(i, previous);
                            seen.clear();
                            continue outer;
                        }
                    }
                    seen.add(n);
                }

                int middleIndex = updateLine.size() / 2;
                sum += updateLine.get(middleIndex);
                break;
            }
        }

        return sum; // Your puzzle answer was 6257
    }

    private record Rule(int before, int after) {
    }
}
