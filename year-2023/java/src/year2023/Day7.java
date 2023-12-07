package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static year2023.Common.readInputLinesForDay;
import static year2023.Common.result;
import static year2023.Common.startPart1;
import static year2023.Common.startPart2;

class Day7 {

    public static void main(String[] args) throws IOException {
        part1();
        part2();
    }

    /**
     * Because the journey will take a few days, she offers to teach you the game of Camel Cards.
     * Camel Cards is sort of similar to poker except it's designed to be easier to play while
     * riding a camel.
     *
     * In Camel Cards, you get a list of hands, and your goal is to order them based on the
     * strength of each hand. A hand consists of five cards labeled one of A, K, Q, J, T, 9, 8, 7,
     * 6, 5, 4, 3, or 2. The relative strength of each card follows this order, where A is the
     * highest and 2 is the lowest.
     *
     * Every hand is exactly one type. From strongest to weakest, they are:
     *
     *   Five of a kind, where all five cards have the same label: AAAAA
     *   Four of a kind, where four cards have the same label and one card has a different label: AA8AA
     *   Full house, where three cards have the same label, and the remaining two cards share a different label: 23332
     *   Three of a kind, where three cards have the same label, and the remaining two cards are each different from any other card in the hand: TTT98
     *   Two pair, where two cards share one label, two other cards share a second label, and the remaining card has a third label: 23432
     *   One pair, where two cards share one label, and the other three cards have a different label from the pair and each other: A23A4
     *   High card, where all cards' labels are distinct: 23456
     *
     * Hands are primarily ordered based on type; for example, every full house is stronger than
     * any three of a kind.
     *
     * If two hands have the same type, a second ordering rule takes effect. Start by comparing the
     * first card in each hand. If these cards are different, the hand with the stronger first card
     * is considered stronger. If the first card in each hand have the same label, however, then
     * move on to considering the second card in each hand. If they differ, the hand with the
     * higher second card wins; otherwise, continue with the third card in each hand, then the
     * fourth, then the fifth.
     *
     * So, 33332 and 2AAAA are both four of a kind hands, but 33332 is stronger because its first
     * card is stronger. Similarly, 77888 and 77788 are both a full house, but 77888 is stronger
     * because its third card is stronger (and both hands have the same first and second card).
     *
     * To play Camel Cards, you are given a list of hands and their corresponding bid (your puzzle
     * input). For example:
     *
     * 32T3K 765
     * T55J5 684
     * KK677 28
     * KTJJT 220
     * QQQJA 483
     *
     * This example shows five hands; each hand is followed by its bid amount. Each hand wins an
     * amount equal to its bid multiplied by its rank, where the weakest hand gets rank 1, the
     * second-weakest hand gets rank 2, and so on up to the strongest hand. Because there are five
     * hands in this example, the strongest hand will have rank 5 and its bid will be multiplied
     * by 5.
     *
     * So, the first step is to put the hands in order of strength:
     *
     *   32T3K is the only one pair and the other hands are all a stronger type, so it gets rank 1.
     *   KK677 and KTJJT are both two pair. Their first cards both have the same label, but the
     *   second card of KK677 is stronger (K vs T), so KTJJT gets rank 2 and KK677 gets rank 3.
     *   T55J5 and QQQJA are both three of a kind. QQQJA has a stronger first card, so it gets
     *   rank 5 and T55J5 gets rank 4.
     *
     * Now, you can determine the total winnings of this set of hands by adding up the result of
     * multiplying each hand's bid with its rank (765 * 1 + 220 * 2 + 28 * 3 + 684 * 4 + 483 * 5).
     * So the total winnings in this example are 6440.
     *
     * Find the rank of every hand in your set. What are the total winnings?
     *
     * Your puzzle answer was 251287184.
     */
    public static void part1() throws IOException {
        startPart1();
        var input = readInputLinesForDay(7);

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

        result(1, result);
    }

    /**
     * To make things a little more interesting, the Elf introduces one additional rule. Now, J
     * cards are jokers - wildcards that can act like whatever card would make the hand the
     * strongest type possible.
     *
     * To balance this, J cards are now the weakest individual cards, weaker even than 2. The other
     * cards stay in the same order: A, K, Q, T, 9, 8, 7, 6, 5, 4, 3, 2, J.
     *
     * J cards can pretend to be whatever card is best for the purpose of determining hand type;
     * for example, QJJQ2 is now considered four of a kind. However, for the purpose of breaking
     * ties between two hands of the same type, J is always treated as J, not the card it's
     * pretending to be: JKKK2 is weaker than QQQQ2 because J is weaker than Q.
     *
     * Now, the above example goes very differently:
     *
     * 32T3K 765
     * T55J5 684
     * KK677 28
     * KTJJT 220
     * QQQJA 483
     *
     *   32T3K is still the only one pair; it doesn't contain any jokers, so its strength doesn't increase.
     *   KK677 is now the only two pair, making it the second-weakest hand.
     *   T55J5, KTJJT, and QQQJA are now all four of a kind! T55J5 gets rank 3, QQQJA gets rank 4, and KTJJT gets rank 5.
     *
     * With the new joker rule, the total winnings in this example are 5905.
     *
     * Using the new joker rule, find the rank of every hand in your set. What are the new total
     * winnings?
     *
     * Your puzzle answer was 250757288.
     */
    public static void part2() throws IOException {
        startPart2();
        var input = readInputLinesForDay(7);

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

        result(2, result);
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

    enum Card {
        _2('2'),
        _3('3'),
        _4('4'),
        _5('5'),
        _6('6'),
        _7('7'),
        _8('8'),
        _9('9'),
        _T('T'),
        _J('J'),
        _Q('Q'),
        _K('K'),
        _A('A');

        private static final Map<Character, Card> LOOKUP = Arrays.stream(values())
                .collect(toUnmodifiableMap(e -> e.key, e -> e));

        private final char key;

        Card(char key) {
            this.key = key;
        }

        static Card of(char ch) {
            return LOOKUP.get(ch);
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
