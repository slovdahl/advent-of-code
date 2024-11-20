package year2023;

import lib.Day;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day4 extends Day {

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        return input
                .map(line -> line.replaceFirst("^Card\\s+\\d+: ", ""))
                .map(line -> line.split("\\|"))
                .map(parts -> new CardData(
                        Arrays.stream(parts[0].trim().split("\\s+"))
                                .map(Integer::parseInt)
                                .collect(Collectors.toUnmodifiableSet()),
                        Arrays.stream(parts[1].trim().split("\\s+"))
                                .map(Integer::parseInt)
                                .toList()
                ))
                .mapToInt(cardData -> cardData.ownNumbers.stream()
                        .filter(cardData.winningNumbers::contains)
                        .reduce(0, (previous, ignored) -> {
                            if (previous == 0) {
                                return 1;
                            } else {
                                return previous * 2;
                            }
                        }))
                .sum(); // Your puzzle answer was 32609
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        Pattern pattern = Pattern.compile("^Card\\s+(\\d+): ([\\s\\d]+) \\| ([\\s\\d]+)$");

        var cards = input
                .map(pattern::matcher)
                .filter(Matcher::matches)
                .map(m ->
                        new LineCardData(
                                Integer.parseInt(m.group(1)),
                                new CardData(
                                        Arrays.stream(m.group(2).trim().split("\\s+"))
                                                .map(Integer::parseInt)
                                                .collect(Collectors.toUnmodifiableSet()),
                                        Arrays.stream(m.group(3).trim().split("\\s+"))
                                                .map(Integer::parseInt)
                                                .toList()
                                )
                        )
                )
                .toList();

        Map<Integer, AtomicInteger> numberOfCards = new HashMap<>();

        cards.stream()
                .map(LineCardData::line)
                .forEach(line -> numberOfCards.put(line, new AtomicInteger(1)));

        for (LineCardData card : cards) {
            int numbersOfThisCard = numberOfCards.get(card.line).get();

            int matchingNumbers = (int) card.data.ownNumbers.stream()
                    .filter(card.data.winningNumbers::contains)
                    .count();

            if (matchingNumbers == 0) {
                continue;
            }

            IntStream.range(card.line + 1, card.line + 1 + matchingNumbers)
                    .forEach(cardNumber -> numberOfCards.get(cardNumber).addAndGet(numbersOfThisCard));
        }

        return numberOfCards.values().stream()
                .map(AtomicInteger::get)
                .mapToInt(v -> v)
                .sum(); // Your puzzle answer was 14624680
    }

    record CardData(Set<Integer> winningNumbers, List<Integer> ownNumbers) {
    }

    record LineCardData(int line, CardData data) {
    }
}
