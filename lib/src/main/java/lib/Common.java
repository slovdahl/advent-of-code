package lib;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Common {

    public static Stream<String> readInputLinesFor(int year, int day) throws IOException {
        Path path = Path.of("year-" + year + "/input/" + day + "/input");

        if (!path.toFile().exists()) {
            path = Path.of("input/" + day + "/input");
            if (!path.toFile().exists()) {
                // TODO: fetch from adventofcode.com
                throw new NoSuchFileException(path.toString());
            }
        }

        return Files.lines(path);
    }

    public static Stream<String> splitOnComma(String input) {
        return Arrays.stream(input.split(","));
    }

    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        } else {
            long gcd = gcd(number1, number2);
            return Math.abs(number1 * number2) / gcd;
        }
    }

    public static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    public static <T> List<List<T>> permutations(List<List<T>> input) {
        List<List<T>> permutations = new ArrayList<>();
        int length = input.size();

        int carry;
        int[] indices = new int[length];

        do {
            List<T> instance = new ArrayList<>();
            for (int i = 0; i < indices.length; i++) {
                int index = indices[i];
                instance.add(input.get(i).get(index));
            }
            permutations.add(instance);

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == input.get(i).size()) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return permutations;
    }

    public static final void swap(char[] a, int i, int j) {
        swap(a, a, i, j);
    }

    public static final void swap(char[] src, char[] dst, int srcIndex, int dstIndex) {
        char t = src[srcIndex];
        src[srcIndex] = dst[dstIndex];
        dst[dstIndex] = t;
    }

    // $ brew install graphviz
    // $ sudo apt-get install graphviz
    // $ neato -Tsvg d25.dot > d25.svg
    public static void visualizeAsDot(PrintStream s, Graph<String> graph) {
        s.println("graph aoc {");

        for (EndpointPair<String> edge : graph.edges()) {
            s.println("  " + edge.nodeU() + " -- " + edge.nodeV());
        }

        s.println("}");
    }
}
