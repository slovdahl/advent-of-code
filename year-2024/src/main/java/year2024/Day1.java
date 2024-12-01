package year2024;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import lib.Day;
import lib.Parse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day1 extends Day {

    private List<Integer> firstColumn;
    private List<Integer> secondColumn;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<List<Integer>> list = input
                .map(Parse::ints)
                .toList();

        firstColumn = new ArrayList<>();
        secondColumn = new ArrayList<>();

        for (List<Integer> row : list) {
            firstColumn.add(row.get(0));
            secondColumn.add(row.get(1));
        }

        firstColumn.sort(Integer::compareTo);
        secondColumn.sort(Integer::compareTo);

        firstColumn = List.copyOf(firstColumn);
        secondColumn = List.copyOf(secondColumn);
    }

    @Override
    protected Object part1(Stream<String> input) {
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

    @Override
    protected Object part2(Stream<String> input) {
        Multiset<Integer> occurrences = HashMultiset.create();
        occurrences.addAll(secondColumn);

        int sum = 0;

        for (Integer n : firstColumn) {
            sum += n * occurrences.count(n);
        }

        return sum;
    }
}
