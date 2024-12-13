package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("unused")
public class Day13 extends Day {

    private static final Pattern BUTTON_PATTERN = Pattern.compile("Button [AB]: X\\+([0-9]+), Y\\+([0-9]+)");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=([0-9]+), Y=([0-9]+)");

    @Override
    protected Mode mode() {
        return Mode.SAMPLE_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<List<String>> sections = Parse.sectionsOfSize(input, 3);

        List<ClawMachine> machines = new ArrayList<>();
        for (List<String> section : sections) {
            String buttonA = section.get(0);
            String buttonB = section.get(1);
            String prize = section.get(2);

            Matcher matcherButtonA = BUTTON_PATTERN.matcher(buttonA);
            checkState(matcherButtonA.find());

            Matcher matcherButtonB = BUTTON_PATTERN.matcher(buttonB);
            checkState(matcherButtonB.find());

            Matcher matcherPrize = PRIZE_PATTERN.matcher(prize);
            checkState(matcherPrize.find());

            ClawMachine clawMachine = new ClawMachine(
                    Integer.parseInt(matcherButtonA.group(1)), Integer.parseInt(matcherButtonA.group(2)),
                    Integer.parseInt(matcherButtonB.group(1)), Integer.parseInt(matcherButtonB.group(2)),
                    Integer.parseInt(matcherPrize.group(1)), Integer.parseInt(matcherPrize.group(2))
            );
            machines.add(clawMachine);
        }

        Map<ClawMachine, Integer> lowestCosts = new HashMap<>();
        for (ClawMachine m : machines) {
            int lowestCost = Integer.MAX_VALUE;

            for (int a = 0; a <= 100; a++) {
                for (int b = 0; b <= 100; b++) {
                    int xResult = m.buttonAX * a + m.buttonBX * b;
                    int yResult = m.buttonAY * a + m.buttonBY * b;
                    if (xResult == m.prizeX && yResult == m.prizeY) {
                        lowestCost = Math.min(lowestCost, a * 3 + b);
                    }
                }
            }

            if (lowestCost < Integer.MAX_VALUE) {
                lowestCosts.put(m, lowestCost);
            }
        }

        return lowestCosts
                .values()
                .stream()
                .mapToLong(cost -> cost)
                .sum();
    }

    record ClawMachine(int buttonAX, int buttonAY, int buttonBX, int buttonBY, long prizeX, long prizeY) {
    }
}
