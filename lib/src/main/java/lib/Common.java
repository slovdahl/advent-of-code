package lib;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class Common {

    public static String repeatWithDelimiter(String needle, int n, String delimiter) {
        return Stream.generate(() -> needle)
                .limit(n)
                .collect(joining(delimiter));
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

    public static int numberOfDigits(long n) {
        if (n == 0) {
            return 1;
        }
        return (int) (Math.log10(n) + 1);
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

    /**
     * Generates strings of the given length with all combinations of the given input characters.
     */
    public static List<String> generate(char[] chars, int length) {
        List<String> result = new ArrayList<>((int) Math.pow(chars.length, length));
        generate(chars, result, "", length);
        return result;
    }

    private static void generate(char[] chars, List<String> values, String current, int length) {
        if (length == 0) {
            values.add(current);
            return;
        }

        for (char c : chars) {
            String newPrefix = current + c;
            generate(chars, values, newPrefix, length - 1);
        }
    }

    /**
     * Generates lists of the given length with all combinations of the given input characters.
     */
    public static <T> List<List<T>> cartesianProduct(Collection<T> options, int length) {
        List<List<T>> result = new ArrayList<>((int) Math.pow(options.size(), length));
        cartesianProduct(options, result, new ArrayList<>(length), length);
        return result;
    }

    private static <T> void cartesianProduct(Collection<T> options, List<List<T>> values, List<T> current, int length) {
        if (length == 0) {
            values.add(current);
            return;
        }

        for (T c : options) {
            List<T> list = new ArrayList<>(length);
            list.addAll(current);
            list.add(c);
            cartesianProduct(options, values, list, length - 1);
        }
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
