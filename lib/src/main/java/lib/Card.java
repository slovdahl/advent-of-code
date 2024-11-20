package lib;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toUnmodifiableMap;

public enum Card {
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

    public static Card of(char ch) {
        return Objects.requireNonNull(LOOKUP.get(ch));
    }
}
