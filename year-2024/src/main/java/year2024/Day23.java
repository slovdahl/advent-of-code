package year2024;

import lib.Day;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day23 extends Day {

    private Map<String, Set<String>> connections;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        connections = new HashMap<>();

        for (String[] pair : input
                .map(line -> line.split("-"))
                .toList()) {

            connections.computeIfAbsent(pair[0], k -> new HashSet<>())
                    .add(pair[1]);

            connections.computeIfAbsent(pair[1], k -> new HashSet<>())
                    .add(pair[0]);
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        Set<Set<String>> setsOfThree = new HashSet<>();
        for (var entry : connections.entrySet()) {
            String computer = entry.getKey();
            Set<String> computerConnections = entry.getValue();
            for (String secondComputer : computerConnections) {
                Set<String> secondComputerConnections = connections.get(secondComputer);
                if (secondComputerConnections.contains(computer)) {
                    for (String thirdComputer : secondComputerConnections) {
                        if (thirdComputer.equals(computer) || thirdComputer.equals(secondComputer)) {
                            continue;
                        }

                        if (computerConnections.contains(thirdComputer)) {
                            setsOfThree.add(Set.of(computer, secondComputer, thirdComputer));
                        }
                    }
                }
            }
        }

        return setsOfThree.stream()
                .filter(s -> s.stream().anyMatch(c -> c.startsWith("t")))
                .count(); // Your puzzle answer was 1314
    }
}
