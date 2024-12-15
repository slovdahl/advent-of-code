package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Gatherers.windowFixed;

@SuppressWarnings("preview")
public class Parse {

    /**
     * Parses the input as space-separated integers.
     */
    public static List<Integer> ints(String input) {
        String[] s = input.trim().split(" ");
        List<Integer> result = new ArrayList<>(s.length);
        for (String str : s) {
            result.add(Integer.parseInt(str.trim()));
        }
        return result;
    }

    /**
     * Parses the input as comma-separated integers.
     */
    public static List<Integer> commaSeparatedInts(String input) {
        String[] s = input.trim().split(",");
        List<Integer> result = new ArrayList<>(s.length);
        for (String str : s) {
            result.add(Integer.parseInt(str.trim()));
        }
        return result;
    }

    /**
     * Parses the input as space-separated longs.
     */
    public static List<Long> longs(String input) {
        String[] s = input.trim().split(" ");
        List<Long> result = new ArrayList<>(s.length);
        for (String str : s) {
            result.add(Long.parseLong(str.trim()));
        }
        return result;
    }

    /**
     * Parses the input as sections optionally separated by empty lines.
     *
     * @param input the input stream to consume
     * @param size the size of each section
     * @return a list of each section
     */
    public static List<List<String>> sectionsOfSize(Stream<String> input, int size) {
        return input
                .filter(s -> !s.isEmpty())
                .gather(windowFixed(size))
                .toList();
    }

    /**
     * Parses the input as sections optionally separated by empty lines.
     *
     * @param input the input stream to consume
     * @return a list of each section
     */
    public static List<List<String>> sections(Stream<String> input) {
        List<List<String>> initial = new ArrayList<>();
        initial.add(new ArrayList<>());

        return input
                .reduce(
                        initial,
                        (List<List<String>> subtotal, String element) -> {
                            if (element.trim().isEmpty()) {
                                subtotal.add(new ArrayList<>());
                            } else {
                                subtotal.getLast().add(element);
                            }
                            return subtotal;
                        },
                        (_, _) -> emptyList()
                );
    }
}
