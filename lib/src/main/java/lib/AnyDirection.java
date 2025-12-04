package lib;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

public enum AnyDirection {
    UP,
    UP_LEFT,
    UP_RIGHT,
    RIGHT,
    DOWN,
    DOWN_LEFT,
    DOWN_RIGHT,
    LEFT;

    public static final Set<AnyDirection> ALL = Arrays.stream(AnyDirection.values())
            .collect(toUnmodifiableSet());

    public AnyDirection opposite() {
        return switch (this) {
            case UP -> DOWN;
            case UP_LEFT -> DOWN_RIGHT;
            case UP_RIGHT -> DOWN_LEFT;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case DOWN_LEFT -> UP_RIGHT;
            case DOWN_RIGHT -> UP_LEFT;
            case LEFT -> RIGHT;
        };
    }
}
