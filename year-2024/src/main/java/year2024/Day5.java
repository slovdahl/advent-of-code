package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import lib.Day;
import lib.Parse;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day5 extends Day {

    private SetMultimap<Integer, Integer> rulePerNumber;
    private List<List<Integer>> updates;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<String> fullInput = input.toList();

        rulePerNumber = fullInput.stream()
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split("\\|"))
                .map(pair -> new Rule(Integer.parseInt(pair[0]), Integer.parseInt(pair[1])))
                .collect(Multimaps.toMultimap(Rule::before, Rule::after, HashMultimap::create));

        updates = fullInput.stream()
                .dropWhile(line -> !line.isEmpty())
                .filter(line -> !line.isEmpty())
                .map(Parse::commaSeparatedInts)
                .collect(Collectors.toList());
    }

    @Override
    protected Object part1(Stream<String> input) {
        return updates.stream()
                .filter(update -> update.stream()
                        .sorted(new RuleComparator(rulePerNumber))
                        .toList()
                        .equals(update)
                )
                .mapToInt(update -> update.get(update.size() / 2))
                .sum(); // Your puzzle answer was 4766
    }

    @Override
    protected Object part2(Stream<String> input) {
        return updates.stream()
                .filter(update -> !update.stream()
                        .sorted(new RuleComparator(rulePerNumber))
                        .toList()
                        .equals(update)
                )
                .map(update -> update.stream()
                        .sorted(new RuleComparator(rulePerNumber))
                        .toList()
                )
                .mapToInt(update -> update.get(update.size() / 2))
                .sum(); // Your puzzle answer was 6257
    }

    private record RuleComparator(SetMultimap<Integer, Integer> rulePerNumber) implements Comparator<Integer> {

        @Override
        public int compare(Integer n1, Integer n2) {
            if (Objects.equals(n1, n2)) {
                return 0;
            }

            Set<Integer> shouldComeAfter = rulePerNumber.get(n1);
            if (!shouldComeAfter.contains(n2)) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    private record Rule(int before, int after) {
    }
}
