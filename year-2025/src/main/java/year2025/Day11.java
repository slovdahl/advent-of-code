package year2025;

import lib.Day;
import lib.Parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;

@SuppressWarnings("unused")
public class Day11 extends Day {

    private List<Device> devices;
    private Map<String, Device> deviceByName;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        devices = input
                .map(line -> {
                    List<String> strings = Parse.strings(line);
                    return new Device(
                            strings.getFirst().substring(0, strings.getFirst().length() - 1),
                            strings.subList(1, strings.size())
                    );
                })
                .toList();

        deviceByName = devices.stream()
                .collect(toUnmodifiableMap(Device::name, device -> device));
    }

    @Override
    protected Object part1(Stream<String> input) {
        Device start = devices.stream()
                .filter(device -> device.name().equals("you"))
                .findFirst()
                .orElseThrow();

        return visitAllOutputs(start, deviceByName); // Your puzzle answer was 472.
    }

    @Override
    protected Object part2(Stream<String> input) {
        Device start = devices.stream()
                .filter(device -> device.name().equals("svr"))
                .findFirst()
                .orElseThrow();

        return visitAllOutputsPart2(
                start,
                deviceByName,
                new HashMap<>(),
                false,
                false); // Your puzzle answer was 526811953334940.
    }

    private static long visitAllOutputs(Device current, Map<String, Device> deviceByName) {
        long paths = 0;
        for (String output : current.outputs()) {
            if (output.equals("out")) {
                paths += 1;
            } else {
                paths += visitAllOutputs(deviceByName.get(output), deviceByName);
            }
        }
        return paths;
    }

    private static long visitAllOutputsPart2(Device current,
                                             Map<String, Device> deviceByName,
                                             Map<VisitedDevice, Long> cache,
                                             boolean visitedFft,
                                             boolean visitedDac) {

        if (current.name().equals("fft")) {
            visitedFft = true;
        }
        if (current.name().equals("dac")) {
            visitedDac = true;
        }

        VisitedDevice visitedDevice = new VisitedDevice(current.name(), visitedFft, visitedDac);
        Long cachedValue = cache.get(visitedDevice);
        if (cachedValue != null) {
            return cachedValue;
        }

        long paths = 0;
        for (String output : current.outputs()) {
            if (output.equals("out")) {
                if (visitedFft && visitedDac) {
                    paths += 1;
                }
            } else {
                paths += visitAllOutputsPart2(deviceByName.get(output), deviceByName, cache, visitedFft, visitedDac);
            }
        }

        cache.put(visitedDevice, paths);

        return paths;
    }

    private record Device(String name, List<String> outputs) {
    }

    private record VisitedDevice(String name, boolean visitedFft, boolean visitedDac) {
    }
}
