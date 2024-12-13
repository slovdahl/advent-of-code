package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("unused")
public class Day13 extends Day {

    private static final Pattern BUTTON_PATTERN = Pattern.compile("Button [AB]: X\\+([0-9]+), Y\\+([0-9]+)");
    private static final Pattern PRIZE_PATTERN = Pattern.compile("Prize: X=([0-9]+), Y=([0-9]+)");

    private List<ClawMachine> machines;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
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

        this.machines = List.copyOf(machines);
    }

    @Override
    protected Object part1(Stream<String> input) {
        return machines.stream()
                .mapToLong(m -> calculateLowestCost(m, 0))
                .sum(); // Your puzzle answer was 33921
    }

    @Override
    protected Object part2(Stream<String> input) {
        return machines.stream()
                .mapToLong(m -> calculateLowestCost(m, 10_000_000_000_000L))
                .sum(); // Your puzzle answer was 82261957837868
    }

    private static long calculateLowestCost(ClawMachine m, long extraDistance) {
        // Cramer's rule https://en.wikipedia.org/wiki/Cramer%27s_rule#Explicit_formulas_for_small_systems
        int denominator = m.buttonAX * m.buttonBY - m.buttonAY * m.buttonBX;
        double a = ((double) (m.prizeX + extraDistance) * m.buttonBY - (m.prizeY + extraDistance) * m.buttonBX) / denominator;
        double b = ((double) m.buttonAX * (m.prizeY + extraDistance) - m.buttonAY * (m.prizeX + extraDistance)) / denominator;

        if (a != (long) a || b != (long) b) {
            return 0;
        } else {
            return (long) a * 3 + (long) b;
        }
    }

    record ClawMachine(int buttonAX, int buttonAY, int buttonBX, int buttonBY, long prizeX, long prizeY) {
    }
}
