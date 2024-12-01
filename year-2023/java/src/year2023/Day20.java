package year2023;

import lib.Day;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@SuppressWarnings({"unused"})
public class Day20 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Long part1(Stream<String> input) throws IOException {
        Stream<String> sampleInput = """
                broadcaster -> a, b, c
                %a -> b
                %b -> c
                %c -> inv
                &inv -> a
                """.lines();

        Stream<String> sampleInput2 = """
                broadcaster -> a
                %a -> inv, con
                &inv -> b
                %b -> con
                &con -> output
                """.lines();

        Map<String, Module> modules = parseAndInitModules(input);

        Queue<InFlightPulse> queue = new LinkedBlockingQueue<>();
        AtomicLong lowPulses = new AtomicLong();
        AtomicLong highPulses = new AtomicLong();

        for (int push = 0; push < 1_000; push++) {
            queue.add(new InFlightPulse(Pulse.LOW, "button", "broadcaster"));

            while (!queue.isEmpty()) {
                InFlightPulse poll = queue.poll();

                if (poll.pulse() == Pulse.LOW) {
                    lowPulses.incrementAndGet();
                } else {
                    highPulses.incrementAndGet();
                }

                Module module = modules.get(poll.target());

                if (module == null) {
                    continue;
                }

                queue.addAll(
                        module.moduleAction()
                                .pulse(poll.pulse(), poll.source())
                );
            }
        }

        return lowPulses.get() * highPulses.get(); // Your puzzle answer was 743090292
    }

    @Override
    protected Long part2(Stream<String> input) throws Exception {
        Map<String, Module> modules = parseAndInitModules(input);

        Queue<InFlightPulse> queue = new ArrayDeque<>();
        boolean machineStarted = false;

        Map<String, Long> lastHighPulseToLl = new HashMap<>();
        Map<String, Long> diffForLastHighPulseToLl = new HashMap<>();

        for (long push = 1; push <= 10_000L; push++) {
            queue.add(new InFlightPulse(Pulse.LOW, "button", "broadcaster"));

            while (!queue.isEmpty()) {
                InFlightPulse poll = queue.poll();

                if (poll.pulse() == Pulse.LOW && poll.target().equals("rx")) {
                    machineStarted = true;
                }

                Module module = modules.get(poll.target());

                if (module == null) {
                    continue;
                }

                if ("ll".equals(module.name()) &&
                        poll.pulse() == Pulse.HIGH &&
                        module.moduleAction() instanceof ConjunctionModule m) {

                    Long prev = lastHighPulseToLl.put(poll.source(), push);
                    long diff = (prev != null ? push - prev : push);
                    diffForLastHighPulseToLl.put(poll.source(), diff);

                    System.out.println(diff + " for " + poll.source() + ": " + m.numberOfHigh + " " + poll);
                }

                queue.addAll(
                        module.moduleAction()
                                .pulse(poll.pulse(), poll.source())
                );
            }

            if (machineStarted) {
                return push;
            }
        }

        return diffForLastHighPulseToLl.values().stream()
                .mapToLong(v -> v)
                .reduce((left, right) -> left * right)
                .orElseThrow(); // Your puzzle answer was 241528184647003
    }

    private static Map<String, Module> parseAndInitModules(Stream<String> input) {
        Map<String, Module> modules = input
                .map(line -> {
                    String[] split = line.split(" ", 3);

                    Type type = Type.from(split[0]);

                    String name;
                    if (type == Type.BROADCASTER) {
                        name = split[0];
                    } else {
                        name = split[0].substring(1);
                    }

                    List<String> connections = Arrays.stream(split[2].split(","))
                            .map(String::trim)
                            .toList();

                    return new Module(
                            name,
                            type,
                            connections,
                            type.moduleAction(name, connections)
                    );
                })
                .collect(toMap(Module::name, module -> module));

        modules.values().forEach(module -> {
            if (module.moduleAction() instanceof ConjunctionModule conjunctionModule) {
                conjunctionModule.initInputStates(modules);
            }
        });

        return modules;
    }

    private record Module(String name, Type type, List<String> connections, ModuleAction moduleAction) {
    }

    private enum Type {
        BROADCASTER {
            @Override
            ModuleAction moduleAction(String name, List<String> connections) {
                return new BroadcasterModule(connections);
            }
        },
        FLIP_FLOP {
            @Override
            ModuleAction moduleAction(String name, List<String> connections) {
                return new FlipFlopModule(name, connections);
            }
        },
        CONJUNCTION {
            @Override
            ModuleAction moduleAction(String name, List<String> connections) {
                return new ConjunctionModule(name, connections);
            }
        };

        abstract ModuleAction moduleAction(String name, List<String> connections);

        static Type from(String name) {
            return switch (name.charAt(0)) {
                case 'b' -> BROADCASTER;
                case '%' -> FLIP_FLOP;
                case '&' -> CONJUNCTION;
                default -> throw new IllegalStateException("Unknown name: " + name);
            };
        }
    }

    private sealed interface ModuleAction {
        List<InFlightPulse> pulse(Pulse pulse, String source);
    }

    private record BroadcasterModule(List<String> connections) implements ModuleAction {

        @Override
        public List<InFlightPulse> pulse(Pulse pulse, String source) {
            return sendPulses(connections, Pulse.LOW, "broadcaster");
        }
    }

    private static final class FlipFlopModule implements ModuleAction {

        private final String name;
        private final List<String> connections;
        private boolean state;

        private FlipFlopModule(String name, List<String> connections) {
            this.name = name;
            this.connections = connections;
            state = false;
        }

        @Override
        public List<InFlightPulse> pulse(Pulse pulse, String source) {
            if (pulse == Pulse.HIGH) {
                return List.of();
            }

            Pulse pulseToSend = state ? Pulse.LOW : Pulse.HIGH;
            state = !state;

            return sendPulses(connections, pulseToSend, name);
        }
    }

    private static final class ConjunctionModule implements ModuleAction {

        private final String name;
        private final List<String> connections;
        private final Map<String, Pulse> inputStates;
        private int numberOfHigh;

        private ConjunctionModule(String name, List<String> connections) {
            this.name = name;
            this.connections = connections;
            inputStates = new HashMap<>();
            numberOfHigh = 0;
        }

        void initInputStates(Map<String, Module> modules) {
            for (Map.Entry<String, Module> entry : modules.entrySet()) {
                if (entry.getKey().equals(name)) {
                    continue;
                }

                boolean hasConnectionToThisModule = entry.getValue().connections().stream()
                        .anyMatch(n -> n.equals(name));

                if (hasConnectionToThisModule) {
                    inputStates.put(entry.getKey(), Pulse.LOW);
                }
            }
        }

        @Override
        public List<InFlightPulse> pulse(Pulse pulse, String source) {
            Pulse previous = inputStates.put(source, pulse);

            if (previous != pulse) {
                if (pulse == Pulse.HIGH) {
                    numberOfHigh++;
                } else {
                    numberOfHigh--;
                }
            }

            boolean allHigh = numberOfHigh == inputStates.size();

            Pulse pulseToSend = allHigh ? Pulse.LOW : Pulse.HIGH;

            return sendPulses(connections, pulseToSend, name);
        }
    }

    private static List<InFlightPulse> sendPulses(List<String> connections, Pulse pulseToSend, String name) {
        List<InFlightPulse> pulses = new ArrayList<>(connections.size());
        for (String connection : connections) {
            pulses.add(new InFlightPulse(pulseToSend, name, connection));
        }
        return pulses;
    }

    private enum Pulse {
        LOW,
        HIGH
    }

    private record InFlightPulse(Pulse pulse, String source, String target) {

        @Override
        public String toString() {
            return "InFlightPulse{" + source + " -" + pulse.name() + "-> " + target + "}";
        }
    }
}
