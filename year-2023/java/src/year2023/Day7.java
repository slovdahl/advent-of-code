package year2023;

import lib.Card;
import lib.Day;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

@SuppressWarnings("unused")
public class Day7 extends Day {

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        List<HandWithType> handWithTypes = input
                .map(line -> line.split(" "))
                .map(parts -> Hand.parse(parts[0], Long.parseLong(parts[1])))
                .map(hand -> new HandWithType(hand, HandType.from(hand)))
                .sorted(new HandComparator((h1, h2) -> {
                    for (int i = 0; i < h1.hand.cards.size(); i++) {
                        Card thisCard = h1.hand.cards.get(i);
                        Card otherCard = h2.hand.cards.get(i);

                        if (thisCard.ordinal() < otherCard.ordinal()) {
                            return -1;
                        } else if (thisCard.ordinal() > otherCard.ordinal()) {
                            return 1;
                        }
                    }

                    return null;
                }))
                .toList();

        long result = 0;
        for (int i = 0; i < handWithTypes.size(); i++) {
            result += handWithTypes.get(i).hand.bid * (i + 1);
        }

        return result; // Your puzzle answer was 251287184
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        List<HandWithType> handWithTypes = input
                .map(line -> line.split(" "))
                .map(parts -> Hand.parse(parts[0], Long.parseLong(parts[1])))
                .map(hand -> new HandWithType(hand, HandType.fromJoker(hand)))
                .sorted(new HandComparator((h1, h2) -> {
                    for (int i = 0; i < h1.hand.cards.size(); i++) {
                        Card thisCard = h1.hand.cards.get(i);
                        Card otherCard = h2.hand.cards.get(i);

                        int thisOrdinal = thisCard == Card._J ? -1 : thisCard.ordinal();
                        int otherOrdinal = otherCard == Card._J ? -1 : otherCard.ordinal();
                        if (thisOrdinal < otherOrdinal) {
                            return -1;
                        } else if (thisOrdinal > otherOrdinal) {
                            return 1;
                        }
                    }

                    return null;
                }))
                .toList();

        long result = 0;
        for (int i = 0; i < handWithTypes.size(); i++) {
            result += handWithTypes.get(i).hand.bid * (i + 1);
        }

        return result; // Your puzzle answer was 250757288
    }

    record Hand(List<Card> cards, Map<Card, Integer> cardToCount, Map<Integer, List<Card>> countToCards, long bid) {
        static Hand parse(String cardsInput, long bid) {
            List<Card> cards = new ArrayList<>(5);
            for (char c : cardsInput.toCharArray()) {
                cards.add(Card.of(c));
            }

            Map<Card, Integer> cardToCount = cards.stream()
                    .collect(groupingBy(Function.identity(), counting()))
                    .entrySet()
                    .stream()
                    .collect(toUnmodifiableMap(Map.Entry::getKey, e -> e.getValue().intValue()));

            Map<Integer, List<Card>> countToCards = cardToCount
                    .entrySet()
                    .stream()
                    .collect(
                            groupingBy(
                                    Map.Entry::getValue,
                                    mapping(
                                            Map.Entry::getKey,
                                            toUnmodifiableList()
                                    )
                            )
                    );

            return new Hand(cards, cardToCount, countToCards, bid);
        }
    }

    record HandWithType(Hand hand, HandType type) {
    }

    static class HandComparator implements Comparator<HandWithType> {

        private final BiFunction<HandWithType, HandWithType, Integer> tieBreakStrategy;

        HandComparator(BiFunction<HandWithType, HandWithType, Integer> tieBreakStrategy) {
            this.tieBreakStrategy = tieBreakStrategy;
        }

        @Override
        public int compare(HandWithType h1, HandWithType h2) {
            if (h1.type.ordinal() < h2.type.ordinal()) {
                return -1;
            } else if (h1.type.ordinal() > h2.type.ordinal()) {
                return 1;
            } else {
                Integer result = tieBreakStrategy.apply(h1, h2);
                if (result != null) {
                    return result;
                }
            }

            return 0;
        }
    }

    enum HandType {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        FIVE_OF_A_KIND;

        static HandType from(Hand hand) {
            Map<Integer, List<Card>> n = hand.countToCards;
            if (n.containsKey(5)) {
                return FIVE_OF_A_KIND;
            } else if (n.containsKey(4)) {
                return FOUR_OF_A_KIND;
            } else if (n.containsKey(3) && n.containsKey(2)) {
                return FULL_HOUSE;
            } else if (n.containsKey(3)) {
                return THREE_OF_A_KIND;
            } else if (n.containsKey(2)) {
                if (n.get(2).size() == 2) {
                    return TWO_PAIR;
                } else if (n.get(2).size() == 1) {
                    return ONE_PAIR;
                } else {
                    throw new IllegalStateException();
                }
            } else if (n.containsKey(1) && n.get(1).size() == 5) {
                return HIGH_CARD;
            } else {
                throw new IllegalStateException();
            }
        }

        static HandType fromJoker(Hand hand) {
            Map<Integer, List<Card>> n = hand.countToCards;
            int numberOfJokers = hand.cardToCount.getOrDefault(Card._J, 0);

            if (n.containsKey(5) ||
                    (n.containsKey(4) && numberOfJokers == 1) ||
                    (n.containsKey(3) && numberOfJokers == 2) ||
                    (n.containsKey(2) && numberOfJokers == 3) ||
                    (n.containsKey(1) && numberOfJokers == 4)) {
                return FIVE_OF_A_KIND;
            } else if (n.containsKey(4) ||
                    (n.containsKey(3) && numberOfJokers == 1) ||
                    (n.containsKey(2) && numberOfJokers == 2 && n.get(2).size() == 2) ||
                    (n.containsKey(1) && numberOfJokers == 3)) {
                return FOUR_OF_A_KIND;
            } else if ((n.containsKey(3) && n.containsKey(2)) ||
                    (n.containsKey(3) && n.containsKey(1) && numberOfJokers == 1) ||
                    (n.getOrDefault(2, List.of()).size() == 2 && numberOfJokers == 1)) {
                return FULL_HOUSE;
            } else if (n.containsKey(3) ||
                    (n.containsKey(2) && numberOfJokers == 1) ||
                    (n.containsKey(1) && numberOfJokers == 2)) {
                return THREE_OF_A_KIND;
            } else if (n.containsKey(2) || numberOfJokers == 1) {
                if (n.getOrDefault(2, List.of()).size() == 2 ||
                        (n.getOrDefault(2, List.of()).size() == 1 && numberOfJokers == 1)) {
                    return TWO_PAIR;
                } else {
                    return ONE_PAIR;
                }
            } else if (n.containsKey(1) && n.get(1).size() == 5) {
                return HIGH_CARD;
            } else {
                throw new IllegalStateException();
            }
        }
    }
}
