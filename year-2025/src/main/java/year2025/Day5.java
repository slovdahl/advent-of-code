package year2025;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import lib.Day;
import lib.Parse;

import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("unused")
public class Day5 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    private List<String> freshInput;
    private List<String> availableInput;

    @Override
    protected void prepare(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);
        checkState(sections.size() == 2);

        freshInput = sections.getFirst();
        availableInput = sections.getLast();
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Range<Long>> ranges = freshInput.stream()
                .map(line -> {
                    String[] split = line.split("-");
                    return Range.closed(Long.parseLong(split[0]), Long.parseLong(split[1]));
                })
                .toList();

        return availableInput.stream()
                .map(Long::parseLong)
                .filter(availableIngredient -> ranges.stream().anyMatch(range -> range.contains(availableIngredient)))
                .count(); // Your puzzle answer was 739.
    }

    @Override
    protected Object part2(Stream<String> input) {
        TreeRangeSet<Long> ranges = freshInput.stream()
                .map(line -> {
                    String[] split = line.split("-");
                    return Range.closed(Long.parseLong(split[0]), Long.parseLong(split[1]));
                })
                .collect(
                        TreeRangeSet::create,
                        TreeRangeSet::add,
                        (r1, r2) -> r1.addAll(r2)
                );

        return ranges.asRanges().stream()
                .mapToLong(r -> r.upperEndpoint() - r.lowerEndpoint() + 1)
                .sum(); // Your puzzle answer was 344486348901788.
    }
}
