package year2024;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import lib.Day;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

@SuppressWarnings("unused")
public class Day23 extends Day {

    private SetMultimap<String, String> connections;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        connections = HashMultimap.create();

        for (String[] pair : input
                .map(line -> line.split("-"))
                .toList()) {

            connections.put(pair[0], pair[1]);
            connections.put(pair[0], pair[0]);
            connections.put(pair[1], pair[0]);
            connections.put(pair[1], pair[1]);
        }
    }

    @Override
    protected Object part1(Stream<String> input) {
        Set<Set<String>> setsOfThree = new HashSet<>();
        for (var entry : Multimaps.asMap(connections).entrySet()) {
            String computer = entry.getKey();
            Set<String> computerConnections = entry.getValue();
            for (String secondComputer : computerConnections) {
                if (computer.equals(secondComputer)) {
                    continue;
                }

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

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        Set<String> largestNetwork = Set.of();

        for (String computer : connections.keySet()) {
            Set<String> computerConnections = connections.get(computer);

            Map<String, Set<String>> intersectionPerConnection = new HashMap<>();
            for (String computerConnection : computerConnections) {
                Set<String> nextConnections = connections.get(computerConnection);
                Sets.SetView<String> intersection = Sets.intersection(computerConnections, nextConnections);
                intersectionPerConnection.put(computerConnection, new HashSet<>(intersection));
            }

            List<Set<String>> networks = intersectionPerConnection.values()
                    .stream()
                    .sorted(comparing(Set::size))
                    .collect(groupingBy(Function.identity(), counting()))
                    .entrySet()
                    .stream()
                    .sorted((o1, o2) -> {
                        if (o1.getValue().equals(o2.getValue())) {
                            if (o1.getKey().equals(o2.getKey())) {
                                return 0;
                            } else {
                                return Integer.compare(o2.getKey().size(), o1.getKey().size());
                            }
                        } else {
                            return Long.compare(o2.getValue(), o1.getValue());
                        }
                    })
                    .map(Map.Entry::getKey)
                    .filter(this::isEveryComputerConnectedToEachOther)
                    .toList();

            for (Set<String> network : networks) {
                if (network.size() > largestNetwork.size()) {
                    largestNetwork = network;
                }
            }
        }

        return largestNetwork.stream()
                .sorted()
                .collect(joining(",")); // Your puzzle answer was bg,bu,ce,ga,hw,jw,nf,nt,ox,tj,uu,vk,wp
    }

    private boolean isEveryComputerConnectedToEachOther(Set<String> candidates) {
        for (String c1 : candidates) {
            for (String c2 : candidates) {
                if (c1.equals(c2)) {
                    continue;
                }

                if (!connections.containsEntry(c1, c2)) {
                    return false;
                }
            }
        }

        return true;
    }
}
