package year2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import lib.Coordinate;
import lib.Day;
import lib.Matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.groupingBy;

@SuppressWarnings("unused")
public class Day14 extends Day {

    private static final Pattern PATTERN = Pattern.compile("p=(-?[0-9]+),(-?[0-9]+) v=(-?[0-9]+),(-?[0-9]+)");

    private List<Robot> robots;
    private Object[][] map;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        robots = new ArrayList<>();

        for (String line : input.toList()) {
            Matcher matcher = PATTERN.matcher(line);
            checkState(matcher.matches());

            robots.add(new Robot(
                    new Coordinate(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(1))),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(3))
            ));
        }

        map = Matrix.objectMatrix(
                mode() == Mode.REAL_INPUT ? 103 : 7,
                mode() == Mode.REAL_INPUT ? 101 : 11, '.'
        );
    }

    @Override
    protected Object part1(Stream<String> input) {
        Multiset<Coordinate> finalPositions = HashMultiset.create();
        for (Robot robot : robots) {
            Coordinate coordinate = robot.position();

            finalPositions.add(
                    robot.position()
                            .moveWithWraparound(map, 100, robot.velocityRow(), robot.velocityColumn())
            );
        }

        finalPositions.removeIf(coordinate -> coordinate.row() == map.length / 2);
        finalPositions.removeIf(coordinate -> coordinate.column() == map[0].length / 2);

        return finalPositions.stream()
                .collect(groupingBy(coordinate -> {
                    int rowQuadrant = coordinate.row() < map.length / 2 ? 0 : 2;
                    int columnQuadrant = coordinate.column() < map[0].length / 2 ? 1 : 2;
                    return rowQuadrant + columnQuadrant;
                }))
                .values()
                .stream()
                .mapToInt(List::size)
                .reduce((left, right) -> left * right)
                .orElseThrow(); // Your puzzle answer was 221142636
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return LongStream.iterate(1, v -> v + 1)
                .limit(10_000)
                .map(n -> {
                    Multiset<Integer> rowCounts = HashMultiset.create();
                    Multiset<Integer> columnCounts = HashMultiset.create();

                    Multiset<Coordinate> positions = HashMultiset.create();
                    for (Robot robot : robots) {
                        Coordinate coordinate = robot.position();

                        Coordinate newPosition = coordinate
                                .moveWithWraparound(map, n, robot.velocityRow(), robot.velocityColumn());

                        rowCounts.add(newPosition.row());
                        columnCounts.add(newPosition.column());

                        positions.add(newPosition);
                    }

                    Integer highestRowCountRow = Multisets.copyHighestCountFirst(rowCounts).stream().iterator().next();
                    int countR = rowCounts.count(highestRowCountRow);

                    Integer highestColumnCountColumn = Multisets.copyHighestCountFirst(columnCounts).stream().iterator().next();
                    int countC = columnCounts.count(highestColumnCountColumn);

                    if (countR > 16 && countC > 30) {
                        return n;
                    } else {
                        return 0L;
                    }
                })
                .filter(n -> n > 0)
                .findFirst()
                .orElseThrow(); // Your puzzle answer was 7916
    }

    private record Robot(Coordinate position, int velocityRow, int velocityColumn) {
    }
}
