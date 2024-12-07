package lib;

import java.util.ArrayList;
import java.util.List;

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
}
