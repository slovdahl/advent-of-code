package year2024;

import lib.Day;
import lib.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Gatherers.windowSliding;

@SuppressWarnings("unused")
public class Day22 extends Day {

    private static final int SEQUENCE_SIZE = 4;

    private List<List<Long>> allSecretNumbers;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        allSecretNumbers = input.map(Long::valueOf)
                .map(initialSecretNumber -> {
                    List<Long> allSecretNumbers = new ArrayList<>(2_000 + 1);
                    allSecretNumbers.add(initialSecretNumber);

                    long secretNumber = initialSecretNumber;
                    for (int i = 0; i < 2_000; i++) {
                        long step1 = ((secretNumber * 64) ^ secretNumber) % 16_777_216;
                        long step2 = ((step1 / 32) ^ step1) % 16_777_216;
                        long step3 = ((step2 * 2048) ^ step2) % 16_777_216;
                        allSecretNumbers.add(step3);
                        secretNumber = step3;
                    }

                    return List.copyOf(allSecretNumbers);
                })
                .toList();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return allSecretNumbers.stream()
                .mapToLong(List::getLast)
                .sum(); // Your puzzle answer was 16953639210
    }

    @Override
    protected Object part2(Stream<String> input) {
        return allSecretNumbers.parallelStream()
                .map(values -> values.stream()
                        .gather(windowSliding(2))
                        .map(elements -> Pair.of(
                                // The diff from the previous price
                                (elements.getLast() % 10) - (elements.getFirst() % 10),
                                // The price
                                elements.getLast() % 10
                        ))
                        .gather(windowSliding(SEQUENCE_SIZE))
                        .map(subset -> Pair.of(
                                // Turn each diff sequence into a string of the diffs concatenated
                                // one after another.
                                subset.stream()
                                        .map(p -> p.first().toString())
                                        .collect(joining()),
                                subset.getLast().second()
                        ))
                        .filter(p -> p.second() > 0)
                        .collect(
                                toMap(
                                        Pair::first, Pair::second, (v1, v2) -> {
                                            // First occurrence wins
                                            return v1;
                                        },
                                        TreeMap::new
                                )
                        )
                )
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .sorted(Map.Entry.comparingByKey())
                .gather(Gatherer.<Map.Entry<String, Long>, State, Long>ofSequential(
                        State::new,
                        Gatherer.Integrator.ofGreedy(
                                (state, element, downstream) -> {
                                    // Sum the number of bananas each monkey can sell for a given price
                                    // change sequence.
                                    if (!element.getKey().equals(state.sequence)) {
                                        if (state.sequence != null) {
                                            downstream.push(state.sum);
                                        }

                                        state.sequence = element.getKey();
                                        state.sum = 0;
                                    }

                                    state.sum += element.getValue();

                                    return true;
                                }
                        )))
                .mapToLong(v -> v)
                .max()
                .orElseThrow(); // Your puzzle answer was 1863
    }

    private static class State {
        private String sequence;
        private long sum;
    }
}
