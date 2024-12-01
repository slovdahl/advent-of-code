package year2023;

import lib.Day;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static lib.Parse.ints;
import static lib.Parse.longs;

@SuppressWarnings("unused")
public class Day6 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        List<String> inputWithoutPrefix = input
                .map(l -> l.substring(10))
                .toList();

        List<Integer> timeInts = ints(inputWithoutPrefix.get(0));
        List<Integer> distanceInts = ints(inputWithoutPrefix.get(1));

        System.out.println(timeInts);
        System.out.println(distanceInts);

        List<Race> races = new ArrayList<>();
        for (int i = 0; i < timeInts.size(); i++) {
            races.add(new Race(timeInts.get(i), distanceInts.get(i)));
        }

        System.out.println(races);

        Map<Race, Integer> numberOfWaysToWin = new HashMap<>();

        for (Race race : races) {
            int winCount = 0;
            boolean seenRange = false;
            for (int timePressed = 1; timePressed < race.time; timePressed++) {
                long distance = race.distanceForTimePressed(timePressed);
                if (distance > race.distanceRecord) {
                    seenRange = true;
                    winCount++;
                } else if (seenRange) {
                    break;
                }
            }

            numberOfWaysToWin.put(race, winCount);
        }

        return numberOfWaysToWin.values().stream()
                .reduce((v1, v2) -> v1 * v2)
                .orElseThrow(); // // Your puzzle answer was 1195150
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        List<String> inputWithoutPrefix = input
                .map(l -> l.substring(10))
                .map(l -> l.replace(" ", ""))
                .toList();

        long timeLong = longs(inputWithoutPrefix.get(0)).get(0);
        long distanceLong = longs(inputWithoutPrefix.get(1)).get(0);

        System.out.println(timeLong);
        System.out.println(distanceLong);

        Race race = new Race(timeLong, distanceLong);

        int winCount = 0;
        boolean seenRange = false;
        for (long timePressed = 1; timePressed < race.time; timePressed++) {
            long distance = race.distanceForTimePressed(timePressed);
            if (distance > race.distanceRecord) {
                seenRange = true;
                winCount++;
            } else if (seenRange) {
                break;
            }
        }

        return winCount; // Your puzzle answer was 42550411
    }

    record Race(long time, long distanceRecord) {
        long distanceForTimePressed(long timePressed) {
            long speed = timePressed;
            long timeForRacing = time - timePressed;
            return speed * timeForRacing;
        }
    }
}
