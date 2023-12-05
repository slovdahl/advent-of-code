package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static eu.lovdahl.advent.of.code.Common.longs;
import static eu.lovdahl.advent.of.code.Common.readInputLinesForDay;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
class Day5 {

    public static void main(String[] args) throws IOException {
        part1();
    }

    /**
     * You barely have time to agree to this request when he brings up another. "While you wait for
     * the ferry, maybe you can help us with our food production problem. The latest Island Island
     * Almanac just arrived and we're having trouble making sense of it."
     *
     * The almanac (your puzzle input) lists all of the seeds that need to be planted. It also
     * lists what type of soil to use with each kind of seed, what type of fertilizer to use with
     * each kind of soil, what type of water to use with each kind of fertilizer, and so on. Every
     * type of seed, soil, fertilizer and so on is identified with a number, but numbers are reused
     * by each category - that is, soil 123 and fertilizer 123 aren't necessarily related to each
     * other.
     *
     * For example:
     *
     * seeds: 79 14 55 13
     *
     * seed-to-soil map:
     * 50 98 2
     * 52 50 48
     *
     * soil-to-fertilizer map:
     * 0 15 37
     * 37 52 2
     * 39 0 15
     *
     * fertilizer-to-water map:
     * 49 53 8
     * 0 11 42
     * 42 0 7
     * 57 7 4
     *
     * water-to-light map:
     * 88 18 7
     * 18 25 70
     *
     * light-to-temperature map:
     * 45 77 23
     * 81 45 19
     * 68 64 13
     *
     * temperature-to-humidity map:
     * 0 69 1
     * 1 0 69
     *
     * humidity-to-location map:
     * 60 56 37
     * 56 93 4
     *
     * The almanac starts by listing which seeds need to be planted: seeds 79, 14, 55, and 13.
     *
     * The rest of the almanac contains a list of maps which describe how to convert numbers from a
     * source category into numbers in a destination category. That is, the section that starts
     * with seed-to-soil map: describes how to convert a seed number (the source) to a soil number
     * (the destination). This lets the gardener and his team know which soil to use with which
     * seeds, which water to use with which fertilizer, and so on.
     *
     * Rather than list every source number and its corresponding destination number one by one,
     * the maps describe entire ranges of numbers that can be converted. Each line within a map
     * contains three numbers: the destination range start, the source range start, and the range
     * length.
     *
     * Consider again the example seed-to-soil map:
     *
     * 50 98 2
     * 52 50 48
     *
     * The first line has a destination range start of 50, a source range start of 98, and a range
     * length of 2. This line means that the source range starts at 98 and contains two values: 98
     * and 99. The destination range is the same length, but it starts at 50, so its two values are
     * 50 and 51. With this information, you know that seed number 98 corresponds to soil number 50
     * and that seed number 99 corresponds to soil number 51.
     *
     * The second line means that the source range starts at 50 and contains 48 values: 50, 51,
     * ..., 96, 97. This corresponds to a destination range starting at 52 and also containing 48
     * values: 52, 53, ..., 98, 99. So, seed number 53 corresponds to soil number 55.
     *
     * Any source numbers that aren't mapped correspond to the same destination number. So, seed
     * number 10 corresponds to soil number 10.
     *
     * So, the entire list of seed numbers and their corresponding soil numbers looks like this:
     *
     * seed  soil
     * 0     0
     * 1     1
     * ...   ...
     * 48    48
     * 49    49
     * 50    52
     * 51    53
     * ...   ...
     * 96    98
     * 97    99
     * 98    50
     * 99    51
     *
     * With this map, you can look up the soil number required for each initial seed number:
     *
     *  Seed number 79 corresponds to soil number 81.
     *  Seed number 14 corresponds to soil number 14.
     *  Seed number 55 corresponds to soil number 57.
     *  Seed number 13 corresponds to soil number 13.
     *
     * The gardener and his team want to get started as soon as possible, so they'd like to know
     * the closest location that needs a seed. Using these maps, find the lowest location number
     * that corresponds to any of the initial seeds. To do this, you'll need to convert each seed
     * number through other categories until you can find its corresponding location number. In
     * this example, the corresponding types are:
     *
     *  Seed 79, soil 81, fertilizer 81, water 81, light 74, temperature 78, humidity 78, location 82.
     *  Seed 14, soil 14, fertilizer 53, water 49, light 42, temperature 42, humidity 43, location 43.
     *  Seed 55, soil 57, fertilizer 57, water 53, light 46, temperature 82, humidity 82, location 86.
     *  Seed 13, soil 13, fertilizer 52, water 41, light 34, temperature 34, humidity 35, location 35.
     *
     * So, the lowest location number in this example is 35.
     *
     * What is the lowest location number that corresponds to any of the initial seed numbers?
     *
     * Your puzzle answer was 199602917.
     */
    public static void part1() throws IOException {
        var input = readInputLinesForDay(5).collect(toList());

        Set<Long> initialSeeds = new HashSet<>(longs(input.removeFirst().replace("seeds: ", "")));
        input.removeFirst();

        List<String> seedToSoilInput = new ArrayList<>();
        List<String> soilToFertilizerInput = new ArrayList<>();
        List<String> fertilizerToWaterInput = new ArrayList<>();
        List<String> waterToLightInput = new ArrayList<>();
        List<String> lightToTemperatureInput = new ArrayList<>();
        List<String> temperatureToHumidityInput = new ArrayList<>();
        List<String> humidityToLocationInput = new ArrayList<>();

        List<String> currentInput = null;

        for (String currentLine : input) {
            if (currentLine.isEmpty()) {
                continue;
            }

            if (currentLine.startsWith("seed-to-soil")) {
                currentInput = seedToSoilInput;
                continue;
            } else if (currentLine.startsWith("soil-to-fertilizer")) {
                currentInput = soilToFertilizerInput;
                continue;
            } else if (currentLine.startsWith("fertilizer-to-water")) {
                currentInput = fertilizerToWaterInput;
                continue;
            } else if (currentLine.startsWith("water-to-light")) {
                currentInput = waterToLightInput;
                continue;
            } else if (currentLine.startsWith("light-to-temperature")) {
                currentInput = lightToTemperatureInput;
                continue;
            } else if (currentLine.startsWith("temperature-to-humidity")) {
                currentInput = temperatureToHumidityInput;
                continue;
            } else if (currentLine.startsWith("humidity-to-location")) {
                currentInput = humidityToLocationInput;
                continue;
            }

            currentInput.add(currentLine);
        }

        Mapper seedToSoil = linesToMapper(seedToSoilInput);
        Mapper soilToFertilizer = linesToMapper(soilToFertilizerInput);
        Mapper fertilizerToWater = linesToMapper(fertilizerToWaterInput);
        Mapper waterToLight = linesToMapper(waterToLightInput);
        Mapper lightToTemperature = linesToMapper(lightToTemperatureInput);
        Mapper temperatureToHumidity = linesToMapper(temperatureToHumidityInput);
        Mapper humidityToLocation = linesToMapper(humidityToLocationInput);

        var result = initialSeeds.stream()
                .map(seedToSoil)
                .map(soilToFertilizer)
                .map(fertilizerToWater)
                .map(waterToLight)
                .map(lightToTemperature)
                .map(temperatureToHumidity)
                .map(humidityToLocation)
                .mapToLong(v -> v)
                .min();

        System.out.println("Part 1: " + result.getAsLong());
    }

    private static Mapper linesToMapper(List<String> lines) {
        List<Range> ranges = new ArrayList<>();
        for (String line : lines) {
            List<Long> currentLineLongs = longs(line);
            Long from = currentLineLongs.get(1);
            Long to = currentLineLongs.get(0);
            Long length = currentLineLongs.get(2);

            ranges.add(
                    new Range(
                            from,
                            from + length - 1,
                            to - from
                    )
            );
        }

        return new Mapper(ranges);
    }

    private record Mapper(List<Range> ranges) implements Function<Long, Long> {

        @Override
        public Long apply(Long input) {
            for (Range range : ranges) {
                if (range.contains(input)) {
                    return range.map(input);
                }
            }

            return input;
        }
    }

    record Range(long start, long end, long diff) {
        boolean contains(long input) {
            return input >= start && input <= end;
        }

        long map(long input) {
            return input + diff;
        }
    }
}