package year2023;

import com.google.common.collect.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static year2023.Common.longs;

@SuppressWarnings("unused")
public class Day5 extends Day {

    @Override
    Object part1(Stream<String> rawInput) throws Exception {
        List<String> input = rawInput.collect(toList());

        LongStream initialSeeds = longs(input.removeFirst().replace("seeds: ", "")).stream().mapToLong(v -> v);
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

        return initialSeeds
                .parallel()
                .map(seedToSoil)
                .map(soilToFertilizer)
                .map(fertilizerToWater)
                .map(waterToLight)
                .map(lightToTemperature)
                .map(temperatureToHumidity)
                .map(humidityToLocation)
                .min()
                .getAsLong(); // Your puzzle answer was 199602917
    }

    @Override
    Object part2(Stream<String> rawInput) throws Exception {
        List<String> input = rawInput.collect(toList());

        List<Long> seedRanges = longs(input.removeFirst().replace("seeds: ", ""));
        input.removeFirst();

        List<Range<Long>> initialSeedRanges = new ArrayList<>();
        for (int i = 0; i < seedRanges.size(); i += 2) {
            long start = seedRanges.get(i);
            long length = seedRanges.get(i + 1);

            initialSeedRanges.add(Range.closed(start, start + length));
        }

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

        RangeMapper seedToSoil = linesToRangeMapper(seedToSoilInput);
        RangeMapper soilToFertilizer = linesToRangeMapper(soilToFertilizerInput);
        RangeMapper fertilizerToWater = linesToRangeMapper(fertilizerToWaterInput);
        RangeMapper waterToLight = linesToRangeMapper(waterToLightInput);
        RangeMapper lightToTemperature = linesToRangeMapper(lightToTemperatureInput);
        RangeMapper temperatureToHumidity = linesToRangeMapper(temperatureToHumidityInput);
        RangeMapper humidityToLocation = linesToRangeMapper(humidityToLocationInput);

        return initialSeedRanges.stream()
                .parallel()
                .map(seedToSoil)
                .flatMap(List::stream)
                .map(soilToFertilizer)
                .flatMap(List::stream)
                .map(fertilizerToWater)
                .flatMap(List::stream)
                .map(waterToLight)
                .flatMap(List::stream)
                .map(lightToTemperature)
                .flatMap(List::stream)
                .map(temperatureToHumidity)
                .flatMap(List::stream)
                .map(humidityToLocation)
                .flatMap(List::stream)
                .mapToLong(Range::lowerEndpoint)
                .min(); // Your puzzle answer was 2254686
    }

    private static Mapper linesToMapper(List<String> lines) {
        List<RangeAndDiff> ranges = new ArrayList<>();
        for (String line : lines) {
            List<Long> currentLineLongs = longs(line);
            long from = currentLineLongs.get(1);
            long to = currentLineLongs.get(0);
            long length = currentLineLongs.get(2);

            ranges.add(
                    new RangeAndDiff(
                            from,
                            from + length - 1,
                            to - from
                    )
            );
        }

        return new Mapper(ranges);
    }


    private static RangeMapper linesToRangeMapper(List<String> lines) {
        List<RangeAndDiff> ranges = new ArrayList<>();
        for (String line : lines) {
            List<Long> currentLineLongs = longs(line);
            long from = currentLineLongs.get(1);
            long to = currentLineLongs.get(0);
            long length = currentLineLongs.get(2);

            ranges.add(
                    new RangeAndDiff(
                            from,
                            from + length - 1,
                            to - from
                    )
            );
        }

        return new RangeMapper(ranges);
    }

    private record Mapper(List<RangeAndDiff> ranges) implements LongUnaryOperator {

        @Override
        public long applyAsLong(long input) {
            for (RangeAndDiff range : ranges) {
                if (range.contains(input)) {
                    return range.map(input);
                }
            }

            return input;
        }
    }

    private record RangeMapper(List<RangeAndDiff> ranges) implements Function<Range<Long>, List<Range<Long>>> {

        @Override
        public List<Range<Long>> apply(Range<Long> input) {
            List<Range<Long>> result = new ArrayList<>();

            Range<Long> remainder = input;

            for (RangeAndDiff range : ranges) {
                if (range.contains(input.lowerEndpoint()) && range.contains(input.upperEndpoint())) {
                    result.add(Range.closed(input.lowerEndpoint() + range.diff, input.upperEndpoint() + range.diff));
                    remainder = Range.openClosed(0L, 0L);
                    break;
                } else if (range.contains(input.lowerEndpoint())) {
                    result.add(Range.closed(input.lowerEndpoint() + range.diff, range.end + range.diff));
                    remainder = Range.closed(range.end + 1, input.upperEndpoint());
                } else if (range.contains(input.upperEndpoint())) {
                    result.add(Range.closed(range.start + range.diff, input.upperEndpoint() + range.diff));
                    remainder = Range.closed(input.lowerEndpoint(), range.start - 1);
                }
            }

            if (!remainder.isEmpty()) {
                result.add(remainder);
            }

            return result;
        }
    }

    record RangeAndDiff(long start, long end, long diff) {
        boolean contains(long input) {
            return input >= start && input <= end;
        }

        long map(long input) {
            return input + diff;
        }
    }
}
