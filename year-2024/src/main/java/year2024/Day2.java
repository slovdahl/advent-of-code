package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day2 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        int valid = 0;

        for (String reportLine : input.toList()) {
            List<Integer> report = Parse.ints(reportLine);

            if (isValidDecreasing(report) || isValidIncreasing(report)) {
                valid++;
            }
        }

        return valid; // Your puzzle answer was 282
    }

    @Override
    protected Object part2(Stream<String> input) {
        int valid = 0;

        for (String reportLine : input.toList()) {
            List<Integer> report = Parse.ints(reportLine);

            if (isValidIncreasing(report) || isValidDecreasing(report)) {
                valid++;
            }
            else {
                for (int i = 0; i < report.size(); i++) {
                    List<Integer> copy = new ArrayList<>(report);
                    copy.remove(i);
                    if (isValidIncreasing(copy) || isValidDecreasing(copy)) {
                        valid++;
                        break;
                    }
                }
            }
        }

        return valid; // Your puzzle answer was 349
    }

    private static boolean isValidIncreasing(List<Integer> report) {
        int previous = report.getFirst();

        for (int i = 1; i < report.size(); i++) {
            int current = report.get(i);
            if (current > previous && current - previous <= 3) {
                previous = current;
            } else {
                return false;
            }
        }

        return true;
    }

    private static boolean isValidDecreasing(List<Integer> report) {
        int previous = report.getFirst();

        for (int i = 1; i < report.size(); i++) {
            int current = report.get(i);
            if (current < previous && previous - current <= 3) {
                previous = current;
            } else {
                return false;
            }
        }

        return true;
    }
}
