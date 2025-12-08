package year2025;

import com.google.common.collect.ImmutableSortedSet;
import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unused")
public class Day8 extends Day {

    private List<Coordinate3d> coordinates;
    private SortedSet<CoordinatePair> shortestPaths;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        coordinates = input
                .map(Parse::commaSeparatedInts)
                .map(l -> new Coordinate3d(l.get(0), l.get(1), l.get(2)))
                .toList();

        SortedSet<CoordinatePair> shortestPaths = new TreeSet<>();
        for (Coordinate3d coordinate : coordinates) {
            shortestPaths.addAll(
                    coordinates.stream()
                            .filter(o -> !coordinate.equals(o))
                            .map(o -> new CoordinatePair(coordinate, o, euclidianDistance(coordinate, o)))
                            .toList()
            );
        }

        this.shortestPaths = ImmutableSortedSet.copyOf(shortestPaths);
    }

    @Override
    protected Object part1(Stream<String> input) {
        TreeSet<CoordinatePair> shortestPaths = new TreeSet<>(this.shortestPaths);
        List<Set<Coordinate3d>> circuits = new ArrayList<>();

        int pairsToConnect = switch (mode()) {
            case REAL_INPUT -> 1000;
            case SAMPLE_INPUT -> 10;
        };

        for (int i = 0; i < pairsToConnect; i++) {
            findMatchingCircuit(shortestPaths, circuits);
        }

        return circuits.stream()
                .sorted(Comparator.<Set<Coordinate3d>, Integer>comparing(Set::size).reversed())
                .limit(3)
                .mapToInt(Set::size)
                .reduce((left, right) -> left * right)
                .getAsInt(); // Your puzzle answer was 175440.
    }

    @Override
    protected Object part2(Stream<String> input) {
        TreeSet<CoordinatePair> shortestPaths = new TreeSet<>(this.shortestPaths);
        List<Set<Coordinate3d>> circuits = new ArrayList<>();

        CoordinatePair lastCoordinatePair = null;
        while (circuits.size() != 1 || circuits.getFirst().size() != coordinates.size()) {
            lastCoordinatePair = findMatchingCircuit(shortestPaths, circuits);
        }

        int x1 = lastCoordinatePair.c1().x();
        int x2 = lastCoordinatePair.c2().x();
        return (long) x1 * x2; // Your puzzle answer was 3200955921.
    }

    private static double euclidianDistance(Coordinate3d c1, Coordinate3d c2) {
        return Math.sqrt(
                Math.pow(c1.x() - c2.x(), 2) +
                        Math.pow(c1.y() - c2.y(), 2) +
                        Math.pow(c1.z() - c2.z(), 2)
        );
    }

    private static CoordinatePair findMatchingCircuit(TreeSet<CoordinatePair> shortestPaths, List<Set<Coordinate3d>> circuits) {
        CoordinatePair coordinatePair = shortestPaths.removeFirst();

        List<Set<Coordinate3d>> matchingCircuits = circuits.stream()
                .filter(circuit -> circuit.stream()
                        .anyMatch(c -> c.equals(coordinatePair.c1()) || c.equals(coordinatePair.c2()))
                )
                .collect(toList());

        if (matchingCircuits.isEmpty()) {
            // New circuit
            Set<Coordinate3d> newCircuit = new HashSet<>();
            newCircuit.add(coordinatePair.c1());
            newCircuit.add(coordinatePair.c2());
            circuits.add(newCircuit);
        } else if (matchingCircuits.size() == 1) {
            // Exactly one, just add the new connection
            matchingCircuits.getFirst().add(coordinatePair.c1());
            matchingCircuits.getFirst().add(coordinatePair.c2());
        } else {
            // Merge two or more existing circuits
            Set<Coordinate3d> circuit = matchingCircuits.removeFirst();
            circuit.add(coordinatePair.c1());
            circuit.add(coordinatePair.c2());
            for (Set<Coordinate3d> matchingCircuit : matchingCircuits) {
                circuit.addAll(matchingCircuit);
                circuits.remove(matchingCircuit);
            }
        }

        return coordinatePair;
    }

    private record Coordinate3d(int x, int y, int z) {
    }

    private record CoordinatePair(Coordinate3d c1, Coordinate3d c2, double distance) implements Comparable<CoordinatePair> {

        @Override
        public int compareTo(CoordinatePair o) {
            return Double.compare(distance, o.distance);
        }
    }
}
