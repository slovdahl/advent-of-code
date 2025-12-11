package year2025;

import lib.Day;
import lib.Parse;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableMap;

@SuppressWarnings("unused")
public class Day11 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<Device> devices = input
                .map(line -> {
                    List<String> strings = Parse.strings(line);
                    return new Device(
                            strings.getFirst().substring(0, strings.getFirst().length() - 1),
                            strings.subList(1, strings.size())
                    );
                })
                .toList();

        Map<String, Device> deviceByName = devices.stream()
                .collect(toUnmodifiableMap(Device::name, device -> device));

        Device start = devices.stream()
                .filter(device -> device.name().equals("you"))
                .findFirst()
                .orElseThrow();

        return visitAllOutputs(start, deviceByName); // Your puzzle answer was 472.
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

    private record Device(String name, List<String> outputs) {
    }
}
