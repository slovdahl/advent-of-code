package year2023;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.readInputLinesForDay;
import static year2023.Common.result;
import static year2023.Common.startPart1;
import static year2023.Common.startPart2;

class Day8 {

    public static void main(String[] args) throws IOException {
        part1();
        part2();
    }

    /**
     * One of the camel's pouches is labeled "maps" - sure enough, it's full of documents (your
     * puzzle input) about how to navigate the desert. At least, you're pretty sure that's what
     * they are; one of the documents contains a list of left/right instructions, and the rest of
     * the documents seem to describe some kind of network of labeled nodes.
     *
     * It seems like you're meant to use the left/right instructions to navigate the network.
     * Perhaps if you have the camel follow the same instructions, you can escape the haunted
     * wasteland!
     *
     * After examining the maps for a bit, two nodes stick out: AAA and ZZZ. You feel like AAA is
     * where you are now, and you have to follow the left/right instructions until you reach ZZZ.
     *
     * This format defines each node of the network individually. For example:
     *
     * RL
     *
     * AAA = (BBB, CCC)
     * BBB = (DDD, EEE)
     * CCC = (ZZZ, GGG)
     * DDD = (DDD, DDD)
     * EEE = (EEE, EEE)
     * GGG = (GGG, GGG)
     * ZZZ = (ZZZ, ZZZ)
     *
     * Starting with AAA, you need to look up the next element based on the next left/right
     * instruction in your input. In this example, start with AAA and go right (R) by choosing the
     * right element of AAA, CCC. Then, L means to choose the left element of CCC, ZZZ. By
     * following the left/right instructions, you reach ZZZ in 2 steps.
     *
     * Of course, you might not find ZZZ right away. If you run out of left/right instructions,
     * repeat the whole sequence of instructions as necessary: RL really means RLRLRLRLRLRLRLRL..
     * . and so on. For example, here is a situation that takes 6 steps to reach ZZZ:
     *
     * LLR
     *
     * AAA = (BBB, BBB)
     * BBB = (AAA, ZZZ)
     * ZZZ = (ZZZ, ZZZ)
     *
     * Starting at AAA, follow the left/right instructions. How many steps are required to reach ZZZ?
     *
     * Your puzzle answer was 18023.
     */
    public static void part1() throws IOException {
        startPart1();

        var input = readInputLinesForDay(8).collect(toList());

        String path = input.removeFirst();

        Pattern linePattern = Pattern.compile("^([A-Z]{3}) = \\(([A-Z]{3}), ([A-Z]{3})\\)$");

        Map<String, Choices> choices = input.stream()
                .filter(line -> !line.isBlank())
                .map(linePattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> Map.entry(matcher.group(1), new Choices(matcher.group(2), matcher.group(3))))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        String currentNode = "AAA";

        long steps = 0;
        while (!currentNode.equals("ZZZ")) {
            Choices c = choices.get(currentNode);

            int choice = (int) (steps % path.length());
            steps++;

            currentNode = switch (path.charAt(choice)) {
                case 'L' -> c.left;
                case 'R' -> c.right;
                default -> throw new IllegalStateException();
            };
        }

        result(1, steps);
    }

    /**
     * The sandstorm is upon you and you aren't any closer to escaping the wasteland. You had the
     * camel follow the instructions, but you've barely left your starting position. It's going to
     * take significantly more steps to escape!
     *
     * What if the map isn't for people - what if the map is for ghosts? Are ghosts even bound by
     * the laws of spacetime? Only one way to find out.
     *
     * After examining the maps a bit longer, your attention is drawn to a curious fact: the number
     * of nodes with names ending in A is equal to the number ending in Z! If you were a ghost,
     * you'd probably just start at every node that ends with A and follow all of the paths at the
     * same time until they all simultaneously end up at nodes that end with Z.
     *
     * For example:
     *
     * LR
     *
     * 11A = (11B, XXX)
     * 11B = (XXX, 11Z)
     * 11Z = (11B, XXX)
     * 22A = (22B, XXX)
     * 22B = (22C, 22C)
     * 22C = (22Z, 22Z)
     * 22Z = (22B, 22B)
     * XXX = (XXX, XXX)
     *
     * Here, there are two starting nodes, 11A and 22A (because they both end with A). As you
     * follow each left/right instruction, use that instruction to simultaneously navigate away
     * from both nodes you're currently on. Repeat this process until all of the nodes you're
     * currently on end with Z. (If only some of the nodes you're on end with Z, they act like any
     * other node and you continue as normal.) In this example, you would proceed as follows:
     *
     *     Step 0: You are at 11A and 22A.
     *     Step 1: You choose all of the left paths, leading you to 11B and 22B.
     *     Step 2: You choose all of the right paths, leading you to 11Z and 22C.
     *     Step 3: You choose all of the left paths, leading you to 11B and 22Z.
     *     Step 4: You choose all of the right paths, leading you to 11Z and 22B.
     *     Step 5: You choose all of the left paths, leading you to 11B and 22C.
     *     Step 6: You choose all of the right paths, leading you to 11Z and 22Z.
     *
     * So, in this example, you end up entirely on nodes that end in Z after 6 steps.
     *
     * Simultaneously start on every node that ends with A. How many steps does it take before
     * you're only on nodes that end with Z?
     *
     * Your puzzle answer was 14449445933179.
     */
    public static void part2() throws IOException {
        startPart2();

        var input = readInputLinesForDay(8).collect(toList());

        String path = input.removeFirst();

        Pattern linePattern = Pattern.compile("^([0-9A-Z]{3}) = \\(([0-9A-Z]{3}), ([0-9A-Z]{3})\\)$");

        Map<String, Choices> choices = input.stream()
                .filter(line -> !line.isBlank())
                .map(linePattern::matcher)
                .filter(Matcher::find)
                .map(matcher -> Map.entry(matcher.group(1), new Choices(matcher.group(2), matcher.group(3))))
                .collect(toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));

        choices
                .keySet()
                .stream()
                .filter(node -> node.charAt(2) == 'A')
                .map(currentNode -> {
                    for (long i = 0; i < Long.MAX_VALUE; i++) {
                        int choiceIndex = (int) (i % path.length());
                        char choice = path.charAt(choiceIndex);

                        Choices c = choices.get(currentNode);
                        currentNode = switch (choice) {
                            case 'L' -> c.left;
                            case 'R' -> c.right;
                            default -> throw new IllegalStateException();
                        };

                        if (currentNode.charAt(2) == 'Z') {
                            return i + 1;
                        }
                    }

                    throw new IllegalStateException();
                })
                .mapToLong(v -> v)
                .reduce(Day8::lcm)
                .ifPresentOrElse(
                        r -> result(2, r),
                        () -> {
                            throw new IllegalStateException();
                        }
                );
    }

    record Choices(String left, String right) {
    }

    static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        } else {
            long gcd = gcd(number1, number2);
            return Math.abs(number1 * number2) / gcd;
        }
    }

    static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }
}
