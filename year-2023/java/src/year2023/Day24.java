package year2023;

import com.google.common.collect.Sets;
import lib.Common;
import lib.Day;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toUnmodifiableSet;

@SuppressWarnings("unused")
public class Day24 extends Day {

    @Override
    protected Long part1(Stream<String> input) throws Exception {
        Stream<String> sampleInput = """
                19, 13, 30 @ -2,  1, -2
                18, 19, 22 @ -1, -1, -2
                20, 25, 34 @ -2, -2, -4
                12, 31, 28 @ -1, -2, -1
                20, 19, 15 @  1, -5, -3
                """.lines();

        Set<Hailstone> hailstones = input
                .map(line -> line.split("@"))
                .map(split -> {
                    String[] initialPositions = split[0].split(",");
                    String[] velocities = split[1].split(",");

                    long x = Long.parseLong(initialPositions[0].trim());
                    long y = Long.parseLong(initialPositions[1].trim());
                    long z = Long.parseLong(initialPositions[2].trim());
                    int deltaX = Integer.parseInt(velocities[0].trim());
                    int deltaY = Integer.parseInt(velocities[1].trim());
                    int deltaZ = Integer.parseInt(velocities[2].trim());

                    long gcd = Common.gcd(deltaX, deltaY);

                    long deltaXMinimized = deltaX / gcd;
                    long deltaYMinimized = deltaY / gcd;

                    double slope = (double) deltaYMinimized / (double) deltaXMinimized;

                    return new Hailstone(x, y, z, deltaX, deltaY, deltaZ, slope);
                })
                .collect(toUnmodifiableSet());

        long lowerBound = hailstones.size() == 5 ? 7 : 200000000000000L;
        long upperBound = hailstones.size() == 5 ? 27 : 400000000000000L;

        Set<Set<Hailstone>> combinations = Sets.combinations(hailstones, 2);

        return combinations.stream()
                .filter(combination -> {
                    Iterator<Hailstone> iterator = combination.iterator();
                    return iterator.next().slope() != iterator.next().slope();
                })
                .map(combination -> {
                    Iterator<Hailstone> iterator = combination.iterator();
                    Hailstone h1 = iterator.next();
                    Hailstone h2 = iterator.next();

                    double a1 = (h1.y() + h1.deltaY()) - h1.y();
                    double b1 = h1.x() - (h1.x() + h1.deltaX());
                    double c1 = a1 * (h1.x()) + b1 * (h1.y());

                    double a2 = (h2.y() + h2.deltaY()) - h2.y();
                    double b2 = h2.x() - (h2.x() + h2.deltaX());
                    double c2 = a2 * (h2.x()) + b2 * (h2.y());

                    double determinant = a1 * b2 - a2 * b1;

                    double xIntersection = h2.deltaX() * (h1.deltaY() * h1.x() + h1.deltaX() * h1.y());

                    double x = (b2 * c1 - b1 * c2) / determinant;
                    double y = (a1 * c2 - a2 * c1) / determinant;

                    if (h1.xIntersectionIsInStartDirection(x) && h1.yIntersectionIsInStartDirection(y) &&
                            h2.xIntersectionIsInStartDirection(x) && h2.yIntersectionIsInStartDirection(y)) {

                        return new Point(x, y);
                    }

                    return Point.INVALID_POINT;
                })
                .filter(point ->
                        point.x() >= lowerBound &&
                                point.x() <= upperBound &&
                                point.y() >= lowerBound &&
                                point.y() <= upperBound
                )
                .count(); // Your puzzle answer was 17244
    }

    private record Hailstone(long x, long y, long z, int deltaX, int deltaY, int deltaZ, double slope) {

        boolean xIntersectionIsInStartDirection(double newX) {
            if (newX >= x) {
                return deltaX >= 0.0;
            } else {
                return deltaX < 0.0;
            }
        }

        boolean yIntersectionIsInStartDirection(double newY) {
            if (newY >= y) {
                return deltaY >= 0.0;
            } else {
                return deltaY < 0.0;
            }
        }
    }

    private record Point(double x, double y) {
        private static final Point INVALID_POINT = new Point(-1, -1);
    }
}
