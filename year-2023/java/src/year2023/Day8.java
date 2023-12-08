package year2023;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.readInputLinesForDay;
import static year2023.Common.result;
import static year2023.Common.startPart1;

class Day8 {

    public static void main(String[] args) throws IOException {
        part1();
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

    record Choices(String left, String right) {
    }
}
