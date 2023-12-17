package year2023;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.permutationsAsStream;

@SuppressWarnings("unused")
public class Day12 extends Day {

    private static final String[] ALTERNATIVES = new String[]{".", "#"};
    private static final Pattern DOT_PATTERN = Pattern.compile("\\.+");

    private Map<Integer, List<String>> permutationsByLength;
    private Cache<String, List<String>> candidateToPermutationsCache;
    private Cache<String, List<String>> unprocessedPermutationsCache;

    @Override
    void prepare(Stream<String> input) {
        permutationsByLength = IntStream.rangeClosed(1, 16)
                .mapToObj(length -> Pair.of(
                        length,
                        permutationsWithUnknownsReplaced("?".repeat(length))
                ))
                .collect(toUnmodifiableMap(Pair::first, Pair::second));

        candidateToPermutationsCache = Caffeine.newBuilder()
                .maximumSize(50_000)
                .build();

        unprocessedPermutationsCache = Caffeine.newBuilder()
                .maximumSize(50_000)
                .build();
    }

    @Override
    Long part1(Stream<String> input) throws IOException {
        return input
                .parallel()
                .map(line -> line.split(" "))
                .map(pair -> new SpringConditionInput(
                        Arrays.stream(DOT_PATTERN.split(pair[0]))
                                .filter(s -> !s.isEmpty())
                                .toList(),
                        Arrays.stream(pair[1].split(","))
                                .map(Integer::parseInt)
                                .toList()
                ))
                .map(sci -> {
                    List<List<String>> unprocessedCandidatePermutations = sci.candidates.stream()
                            .map(this::candidateToPermutations)
                            .toList();

                    return permutationsAsStream(unprocessedCandidatePermutations)
                            .map(permutationsThatCanBeSplit -> permutationsThatCanBeSplit.stream()
                                    .mapMulti(
                                            (String candidate, Consumer<String> consumer) -> {
                                                int hasDot = candidate.indexOf(".");
                                                if (hasDot >= 0) {
                                                    StringTokenizer tokenizer = new StringTokenizer(candidate, ".", false);
                                                    while (tokenizer.hasMoreTokens()) {
                                                        String next = tokenizer.nextToken();
                                                        if (!next.isEmpty()) {
                                                            consumer.accept(next);
                                                        }
                                                    }
                                                } else {
                                                    consumer.accept(candidate);
                                                }
                                            }
                                    )
                                    .toList()
                            )
                            .filter(permutation -> isValid(permutation, sci.contiguousGroupSizes))
                            .count();
                })
                .mapToLong(v -> v)
                .sum(); // Your puzzle answer was 7506
    }

    @Override
    Long part2(Stream<String> input) throws Exception {
        return input
                .parallel()
                .map(line -> line.split(" "))
                .map(pair -> new String[]{
                        IntStream.range(0, 5)
                                .mapToObj(n -> pair[0])
                                .collect(joining("?")),
                        IntStream.range(0, 5)
                                .mapToObj(n -> pair[1])
                                .collect(joining(","))
                })
                .map(pair -> new SpringConditionInput(
                        Arrays.stream(DOT_PATTERN.split(pair[0]))
                                .filter(s -> !s.isEmpty())
                                .toList(),
                        Arrays.stream(pair[1].split(","))
                                .map(Integer::parseInt)
                                .toList()
                ))
                .map(sci -> {
                    List<List<String>> unprocessedCandidatePermutations = sci.candidates.stream()
                            .map(this::candidateToPermutations)
                            .toList();

                    return permutationsAsStream(unprocessedCandidatePermutations)
                            .map(permutationsThatCanBeSplit -> permutationsThatCanBeSplit.stream()
                                    .mapMulti(
                                            (String candidate, Consumer<String> consumer) -> {
                                                int hasDot = candidate.indexOf(".");
                                                if (hasDot >= 0) {
                                                    List<String> processed = unprocessedPermutationsCache.get(candidate, c -> {
                                                        List<String> result = new ArrayList<>();
                                                        StringTokenizer tokenizer = new StringTokenizer(c, ".", false);
                                                        while (tokenizer.hasMoreTokens()) {
                                                            String next = tokenizer.nextToken();
                                                            if (!next.isEmpty()) {
                                                                result.add(next);
                                                            }
                                                        }
                                                        return result;
                                                    });

                                                    for (String s : processed) {
                                                        consumer.accept(s);
                                                    }
                                                } else {
                                                    consumer.accept(candidate);
                                                }
                                            }
                                    )
                                    .toList()
                            )
                            .filter(permutation -> isValid(permutation, sci.contiguousGroupSizes))
                            .count();
                })
                .mapToLong(v -> v)
                .sum();
    }

    private List<String> candidateToPermutations(String candidate) {
        return candidateToPermutationsCache.get(candidate, c -> {
            if (!c.contains("#")) {
                return permutationsByLength.get(c.length());
            }

            return permutationsWithUnknownsReplaced(c);
        });
    }

    static List<String> permutationsOfBrokenAndWorking(int length) {
        int n = (int) Math.pow(ALTERNATIVES.length, length);
        List<String> combinations = new ArrayList<>(n);

        int carry;
        int[] indices = new int[length];
        do {
            StringBuilder sb = new StringBuilder(length);
            for (int index : indices) {
                sb.append(ALTERNATIVES[index]);
            }
            combinations.add(sb.toString());

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == ALTERNATIVES.length) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return combinations;
    }

    static List<String> permutationsWithUnknownsReplaced(String input) {
        Set<Integer> indicesToReplace = new HashSet<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '?') {
                indicesToReplace.add(i);
            }
        }

        if (indicesToReplace.isEmpty()) {
            return List.of(input);
        }

        List<String> combinations = new ArrayList<>();
        int length = input.length();

        int carry;
        int[] indices = new int[length];

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indices.length; i++) {
                if (indicesToReplace.contains(i)) {
                    sb.append(ALTERNATIVES[indices[i]]);
                } else {
                    sb.append(input.charAt(i));
                }
            }
            combinations.add(sb.toString());

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (!indicesToReplace.contains(i)) {
                    continue;
                }

                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == ALTERNATIVES.length) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return combinations;
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

    record SpringConditionInput(List<String> candidates, List<Integer> contiguousGroupSizes) {
    }
}
