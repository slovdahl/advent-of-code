package year2023;

import lib.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static lib.Common.permutations;

@SuppressWarnings("unused")
public class Day12 extends Day {

    private static final Pattern DOT_PATTERN = Pattern.compile("\\.+");

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) throws IOException {
        return input
                .parallel()
                .map(line -> line.split(" "))
                .map(pair -> new SpringConditionInput(
                        pair[0],
                        Arrays.stream(DOT_PATTERN.split(pair[0]))
                                .filter(s -> !s.isEmpty())
                                .toList(),
                        Arrays.stream(pair[1].split(","))
                                .map(Integer::parseInt)
                                .toList()
                ))
                .map(sci -> {
                    List<List<String>> unprocessedCandidatePermutations = sci.candidates.stream()
                            .map(Day12::permutationsWithUnknownsReplaced)
                            .toList();

                    List<List<String>> allPermutations = permutations(unprocessedCandidatePermutations)
                            .stream()
                            .map(permutationsThatCanBeSplit -> permutationsThatCanBeSplit.stream()
                                    .mapMulti(
                                            (String candidate, Consumer<String> consumer) -> {
                                                if (candidate.contains(".")) {
                                                    DOT_PATTERN.splitAsStream(candidate)
                                                            .filter(s -> !s.isEmpty())
                                                            .forEachOrdered(consumer);
                                                }
                                                else {
                                                    consumer.accept(candidate);
                                                }
                                            }
                                    )
                                    .toList()
                            )
                            .toList();

                    return allPermutations.stream()
                            .filter(permutation -> isValid(permutation, sci.contiguousGroupSizes))
                            .count();
                })
                .mapToLong(v -> v)
                .sum(); // Your puzzle answer was 7506
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

    static List<String> permutationsWithUnknownsReplaced(String input) {
        String[] alternatives = {".", "#"};
        List<String> combinations = new ArrayList<>();
        int length = input.length();

        Set<Integer> indicesToReplace = new HashSet<>();
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '?') {
                indicesToReplace.add(i);
            }
        }

        if (indicesToReplace.isEmpty()) {
            return List.of(input);
        }

        int carry;
        int[] indices = new int[length];

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < indices.length; i++) {
                if (indicesToReplace.contains(i)) {
                    sb.append(alternatives[indices[i]]);
                }
                else {
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
