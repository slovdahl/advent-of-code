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
    void prepare(Stream<String> input) {
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
