package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import lib.Day;
import lib.Parse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day5 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<String> fullInput = input.toList();

        List<Rule> rules = fullInput.stream()
                .takeWhile(line -> !line.isEmpty())
                .map(line -> line.split("\\|"))
                .map(pair -> new Rule(Integer.parseInt(pair[0]), Integer.parseInt(pair[1])))
                .toList();

        SetMultimap<Integer, Integer> rulePerNumber = HashMultimap.create();
        for (Rule rule : rules) {
            rulePerNumber.put(rule.before(), rule.after());
        }

        List<List<Integer>> updateLines = fullInput.stream()
                .dropWhile(line -> !line.isEmpty())
                .filter(line -> !line.isEmpty())
                .map(Parse::commaSeparatedInts)
                .collect(Collectors.toList());

        Iterator<List<Integer>> iterator = updateLines.iterator();
        while (iterator.hasNext()) {
            List<Integer> updateLine = iterator.next();
            Set<Integer> seen = new HashSet<>();

            outer:
            for (Integer n : updateLine) {
                Set<Integer> shouldComeAfter = rulePerNumber.get(n);

                for (Integer s : seen) {
                    if (shouldComeAfter.contains(s)) {
                        iterator.remove();
                        break outer;
                    }
                }

                seen.add(n);
            }
        }

        int sum = 0;
        for (List<Integer> updateLine : updateLines) {
            int middleIndex = updateLine.size() / 2;
            sum += updateLine.get(middleIndex);
        }

        return sum; // Your puzzle answer was 4766
    }

    private record Rule(int before, int after) {
    }
}
