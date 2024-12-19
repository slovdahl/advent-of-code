package year2024;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lib.Day;
import lib.Parse;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

@SuppressWarnings("unused")
public class Day19 extends Day {

    private Map<Character, List<String>> availablePatterns;
    private List<String> designs;
    private Cache<String, Boolean> cachedMatches;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        List<List<String>> sections = Parse.sections(input);

        availablePatterns = Parse.splitOnComma(sections.getFirst().getFirst()).toList().stream()
                .filter(p -> p.length() > 1)
                .sorted(comparing(String::length).reversed())
                .collect(groupingBy(p -> p.charAt(0)));

        designs = sections.get(1);

        cachedMatches = Caffeine.newBuilder()
                .maximumSize(10_000)
                .build();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return designs.stream()
                .filter(this::matchAgainstPatterns)
                .count(); // Your puzzle answer was 317
    }

    private boolean matchAgainstPatterns(String needle) {
        if (needle.isEmpty()) {
            return true;
        }

        Boolean cached = cachedMatches.getIfPresent(needle);
        if (cached != null) {
            return cached;
        }

        char firstChar = needle.charAt(0);
        for (String availablePattern : availablePatterns.getOrDefault(firstChar, List.of())) {
            if (needle.startsWith(availablePattern)) {
                if (matchAgainstPatterns(needle.substring(availablePattern.length()))) {
                    cachedMatches.put(needle, Boolean.TRUE);
                    return true;
                }
            }
        }

        cachedMatches.put(needle, Boolean.FALSE);
        return false;
    }
}
