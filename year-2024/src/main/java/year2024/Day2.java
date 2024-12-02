package year2024;

import lib.Day;
import lib.Parse;

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

        outer:
        for (String reportLine : input.toList()) {
            List<Integer> report = Parse.ints(reportLine);
            int first = report.get(0);
            int second = report.get(1);

            int previous = first;
            if (second > first) {
                // increasing
                for (int i = 1; i < report.size(); i++) {
                    int current = report.get(i);
                    if (current > previous && current - previous <= 3) {
                        previous = current;
                    } else {
                        continue outer;
                    }
                }
                valid++;
            } else if (first > second) {
                // decreasing
                for (int i = 1; i < report.size(); i++) {
                    int current = report.get(i);
                    if (current < previous && previous - current <= 3) {
                        previous = current;
                    } else {
                        continue outer;
                    }
                }
                valid++;
            }
        }

        return valid; // Your puzzle answer was 282
    }
}
