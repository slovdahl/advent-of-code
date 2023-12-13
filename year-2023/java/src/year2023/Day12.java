package year2023;

import com.google.common.base.CharMatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.permutations;

@SuppressWarnings("unused")
public class Day12 extends Day {

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.+");

    private Map<Integer, Map<Integer, List<String>>> permutationsByLength;

    @Override
    void prepare() {
        permutationsByLength = IntStream.rangeClosed(1, 16)
                .mapToObj(length -> Pair.of(
                        length,
                        IntStream.rangeClosed(1, 16)
                                .mapToObj(brokenPipes -> Pair.of(
                                        brokenPipes,
                                        permutationsOfBrokenAndWorking(length).stream()
                                                .filter(combination -> CharMatcher.is('#').countIn(combination) == brokenPipes)
                                                .toList()
                                ))
                                .collect(toUnmodifiableMap(Pair::first, Pair::second)))
                )
                .collect(toUnmodifiableMap(Pair::first, Pair::second));
    }

    /**
     * "Hello! What brings you to the hot springs today? Sorry they're not very hot right now;
     * we're having a lava shortage at the moment." You ask about the missing machine parts for
     * Desert Island.
     *
     * "Oh, all of Gear Island is currently offline! Nothing is being manufactured at the moment,
     * not until we get more lava to heat our forges. And our springs. The springs aren't very
     * springy unless they're hot!"
     *
     * "Say, could you go up and see why the lava stopped flowing? The springs are too cold for
     * normal operation, but we should be able to find one springy enough to launch you up there!"
     *
     * There's just one problem - many of the springs have fallen into disrepair, so they're not
     * actually sure which springs would even be safe to use! Worse yet, their condition records of
     * which springs are damaged (your puzzle input) are also damaged! You'll need to help them
     * repair the damaged records.
     *
     * In the giant field just outside, the springs are arranged into rows. For each row, the
     * condition records show every spring and whether it is operational (.) or damaged (#). This
     * is the part of the condition records that is itself damaged; for some springs, it is simply
     * unknown (?) whether the spring is operational or damaged.
     *
     * However, the engineer that produced the condition records also duplicated some of this
     * information in a different format! After the list of springs for a given row, the size of
     * each contiguous group of damaged springs is listed in the order those groups appear in the
     * row. This list always accounts for every damaged spring, and each number is the entire size
     * of its contiguous group (that is, groups are always separated by at least one operational
     * spring: #### would always be 4, never 2,2).
     *
     * So, condition records with no unknown spring conditions might look like this:
     *
     * #.#.### 1,1,3
     * .#...#....###. 1,1,3
     * .#.###.#.###### 1,3,1,6
     * ####.#...#... 4,1,1
     * #....######..#####. 1,6,5
     * .###.##....# 3,2,1
     *
     * However, the condition records are partially damaged; some of the springs' conditions are
     * actually unknown (?). For example:
     *
     * ???.### 1,1,3
     * .??..??...?##. 1,1,3
     * ?#?#?#?#?#?#?#? 1,3,1,6
     * ????.#...#... 4,1,1
     * ????.######..#####. 1,6,5
     * ?###???????? 3,2,1
     *
     * Equipped with this information, it is your job to figure out how many different arrangements
     * of operational and broken springs fit the given criteria in each row.
     *
     * In the first line (???.### 1,1,3), there is exactly one way separate groups of one, one, and
     * three broken springs (in that order) can appear in that row: the first three unknown springs
     * must be broken, then operational, then broken (#.#), making the whole row #.#.###.
     *
     * The second line is more interesting: .??..??...?##. 1,1,3 could be a total of four different
     * arrangements. The last ? must always be broken (to satisfy the final contiguous group of
     * three broken springs), and each ?? must hide exactly one of the two broken springs. (Neither
     * ?? could be both broken springs or they would form a single contiguous group of two; if that
     * were true, the numbers afterward would have been 2,3 instead.) Since each ?? can either be
     * #. or .#, there are four possible arrangements of springs.
     *
     * The last line is actually consistent with ten different arrangements! Because the first
     * number is 3, the first and second ? must both be . (if either were #, the first number would
     * have to be 4 or higher). However, the remaining run of unknown spring conditions have many
     * different ways they could hold groups of two and one broken springs:
     *
     * ?###???????? 3,2,1
     * .###.##.#...
     * .###.##..#..
     * .###.##...#.
     * .###.##....#
     * .###..##.#..
     * .###..##..#.
     * .###..##...#
     * .###...##.#.
     * .###...##..#
     * .###....##.#
     *
     * In this example, the number of
     *
     * possible arrangements for each row is:
     *
     *     ???.### 1,1,3 - 1 arrangement
     *     .??..??...?##. 1,1,3 - 4 arrangements
     *     ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
     *     ????.#...#... 4,1,1 - 1 arrangement
     *     ????.######..#####. 1,6,5 - 4 arrangements
     *     ?###???????? 3,2,1 - 10 arrangements
     *
     * Adding all of the possible arrangement counts together produces a total of 21 arrangements.
     *
     * For each row, count all of the different arrangements of operational and broken springs that
     * meet the given criteria. What is the sum of those counts?
     */
    @Override
    Object part1(Stream<String> input) throws IOException {
        return input
                .parallel()
                .map(line -> line.split(" "))
                .map(pair -> new SpringConditionInput(
                        pair[0],
                        springConditionsToList(pair[0]),
                        Arrays.stream(pair[1].split(","))
                                .map(Integer::parseInt)
                                .toList()
                ))
                .map(sci -> {
                    List<List<String>> unprocessedCandidatePermutations = sci.candidates.stream()
                            .map(this::candidateToPermutations)
                            .toList();

                    List<List<String>> allPermutations = permutations(unprocessedCandidatePermutations)
                            .stream()
                            .map(candidatesThatCanBeSplit -> candidatesThatCanBeSplit.stream()
                                    .mapMulti(
                                            (String candidate, Consumer<String> consumer) -> Arrays.stream(DOT_PATTERN.split(candidate))
                                                    .filter(s -> !s.isEmpty())
                                                    .forEachOrdered(consumer)
                                    )
                                    .toList()
                            )
                            .toList();

                    return allPermutations.stream()
                            .filter(permutation -> isValid(permutation, sci.contiguousGroupSizes))
                            .count();
                })
                .mapToLong(v -> v)
                .sum();

        // 5808 too low
    }

    private List<String> candidateToPermutations(String candidate) {
        if (!candidate.contains("#")) {
            return permutationsByLength.get(candidate.length())
                    .values()
                    .stream()
                    .flatMap(Collection::stream)
                    .toList();
        } else if (!candidate.contains("?")) {
            return List.of(candidate);
        }

        return permutationsOfUnknownsReplaced(candidate);
    }

    static List<String> springConditionsToList(String c) {
        return Arrays.stream(DOT_PATTERN.split(c))
                .filter(s -> !s.isEmpty())
                .toList();
    }

    boolean isValid(List<String> candidates, List<Integer> groupSizes) {
        if (candidates.size() != groupSizes.size()) {
            return false;
        }

        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).length() != groupSizes.get(i)) {
                return false;
            }
        }

        return true;
    }

    static List<String> permutationsOfBrokenAndWorking(int length) {
        String[] chars = {".", "#"};
        int n = (int) Math.pow(chars.length, length);
        List<String> combinations = new ArrayList<>(n);

        int carry;
        int[] indices = new int[length];
        do {
            StringBuilder sb = new StringBuilder(length);
            for (int index : indices) {
                sb.append(chars[index]);
            }
            combinations.add(sb.toString());

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == chars.length) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return combinations;
    }

    static List<String> permutationsOfUnknownsReplaced(String input) {
        String[] alternatives = {".", "#"};
        List<String> combinations = new ArrayList<>();
        int length = input.length();

        Set<Integer> indicesToCombine = new HashSet<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '?') {
                indicesToCombine.add(i);
            }
        }

        int carry;
        int[] indices = new int[length];

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indices.length; i++) {
                if (indicesToCombine.contains(i)) {
                    sb.append(alternatives[indices[i]]);
                }
                else {
                    sb.append(input.charAt(i));
                }
            }
            combinations.add(sb.toString());

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (!indicesToCombine.contains(i)) {
                    continue;
                }

                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == alternatives.length) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return List.copyOf(combinations);
    }

    record SpringConditionInput(String springConditions, List<String> candidates, List<Integer> contiguousGroupSizes) {
    }
}
