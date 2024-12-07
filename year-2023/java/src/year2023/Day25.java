package year2023;

import com.google.common.collect.Iterators;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.visualizeAsDot;

@SuppressWarnings("unused")
public class Day25 extends Day {

    @Override
    Integer part1(Stream<String> input) throws Exception {
        Stream<String> sampleInput = """
                jqt: rhn xhk nvd
                rsh: frs pzl lsr
                xhk: hfx
                cmg: qnr nvd lhk bvb
                rhn: xhk bvb hfx
                bvb: xhk hfx
                pzl: lsr hfx nvd
                qnr: nvd
                ntq: jqt hfx bvb xhk
                nvd: lhk
                lsr: lhk
                rzs: qnr cmg lsr rsh
                frs: qnr lhk lsr
                """.lines();

        Map<String, String[]> nameToConnections = input
                .map(line -> line.split(":"))
                .collect(toUnmodifiableMap(pair -> pair[0], pair -> pair[1].trim().split(" ")));

        Map<String, Component> components = nameToConnections.keySet().stream()
                .map(name -> new Component(name, new HashSet<>()))
                .collect(toMap(Component::name, component -> component));

        for (Map.Entry<String, String[]> entry : nameToConnections.entrySet()) {
            components.get(entry.getKey()).connections().addAll(
                    Arrays.stream(entry.getValue())
                            .map(component -> components.computeIfAbsent(component, c -> new Component(c, new HashSet<>())))
                            .toList()
            );
        }

        Component componentWithMostLinks = components.values().stream()
                .max(comparing(Component::numberOfLinks))
                .orElseThrow();

        MutableGraph<String> graph = GraphBuilder.undirected()
                .allowsSelfLoops(false)
                .build();

        for (Map.Entry<String, String[]> entry : nameToConnections.entrySet()) {
            for (String connection : entry.getValue()) {
                graph.putEdge(entry.getKey(), connection);
            }
        }

        try (var stream = new PrintStream("d25.dot")) {
            visualizeAsDot(stream, graph);

            // $ brew install graphviz
            // $ neato -Tsvg d25.dot > d25.svg
            // open the SVG
            //
            // sph - rkh
            // hrs - mnf
            // nnl - kpc
        }

        graph.removeEdge("sph", "rkh");
        graph.removeEdge("hrs", "mnf");
        graph.removeEdge("nnl", "kpc");

        return Iterators.size(Traverser.forGraph(graph).breadthFirst("sph").iterator()) *
                Iterators.size(Traverser.forGraph(graph).breadthFirst("rkh").iterator()); // Your puzzle answer was 614655
    }

    private record Component(String name, Set<Component> connections) {

        int numberOfLinks() {
            int n = connections.size();
            for (Component connection : connections) {
                n += connection.numberOfLinks();
            }
            return n;
        }
    }
}
