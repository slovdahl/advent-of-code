package year2024;

import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day1 extends Day {

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected Object part1(Stream<String> input) {
        List<List<Integer>> list = input
                .map(Parse::ints)
                .toList();

        List<Integer> firstColumn = new ArrayList<>();
        List<Integer> secondColumn = new ArrayList<>();

        for (List<Integer> row : list) {
            firstColumn.add(row.get(0));
            secondColumn.add(row.get(1));
        }

        firstColumn.sort(Integer::compareTo);
        secondColumn.sort(Integer::compareTo);

        int sum = 0;

        Iterator<Integer> firstIterator = firstColumn.iterator();
        Iterator<Integer> secondIterator = secondColumn.iterator();

        while (firstIterator.hasNext() && secondIterator.hasNext()) {
            Integer first = firstIterator.next();
            Integer second = secondIterator.next();
            int distance = Math.abs(second - first);
            sum += distance;
        }

        return sum; // Your puzzle answer was 2164381
    }
}
