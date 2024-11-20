package year2023;

import com.google.common.collect.Iterators;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import lib.Day;

import java.io.PrintStream;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;
import static lib.Common.visualizeAsDot;

@SuppressWarnings("unused")
public class Day25 extends Day {

    @Override
    protected Integer part1(Stream<String> input) throws Exception {
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
}
