package year2025;

import com.google.common.collect.Range;
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

    @Override
    protected Object part1(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);
        checkState(sections.size() == 2);

        List<String> freshInput = sections.getFirst();
        List<String> availableInput = sections.getLast();

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
}
