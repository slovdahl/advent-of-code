package year2024;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lib.Day;
import lib.Parse;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day19 extends Day {

    private List<String> availablePatterns;
    private List<String> designs;
    private Cache<String, Long> cachedMatches;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        List<List<String>> sections = Parse.sections(input);

        availablePatterns = Parse.splitOnComma(sections.getFirst().getFirst()).toList();

        designs = sections.get(1);

        cachedMatches = Caffeine.newBuilder()
                .maximumSize(50)
                .build();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return designs.stream()
                .filter(needle -> matchAgainstPatterns(needle, false) > 0)
                .count(); // Your puzzle answer was 317
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        cachedMatches.invalidateAll();
        return designs.stream()
                .mapToLong(needle -> matchAgainstPatterns(needle, true))
                .sum(); // Your puzzle answer was 883443544805484
    }

    private long matchAgainstPatterns(String needle, boolean matchAll) {
        if (needle.isEmpty()) {
            return 1L;
        }

        Long cached = cachedMatches.getIfPresent(needle);
        if (cached != null) {
            return cached;
        }

        long count = 0L;
        for (String availablePattern : availablePatterns) {
            if (needle.startsWith(availablePattern)) {
                count += matchAgainstPatterns(needle.substring(availablePattern.length()), matchAll);
                if (count > 0L && !matchAll) {
                    break;
                }
            }
        }

        cachedMatches.put(needle, count);
        return count;
    }
}
