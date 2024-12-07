package lib;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {

    public static final Comparator<Class<?>> DAY_CLASS_COMPARATOR = Comparator
            // Hack to get sorting of Day1, Day2, .., Day10, Day 11 to work as expected
            // without actually comparing the integers.
            .<Class<?>, Integer>comparing(cls -> cls.getSimpleName().length())
            .thenComparing(Class::getName);

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("Day(\\d+)");

    public static void run(String[] args, List<? extends Class<?>> dayClasses) throws Exception {
        Class<?> clazz;
        if (args.length > 0) {
            int day = Integer.parseInt(args[0]);
            clazz = dayClasses.stream()
                    .filter(cls -> cls.getSimpleName().equals("Day" + day))
                    .findFirst()
                    .orElseThrow();
        } else {
            clazz = dayClasses.getLast();
        }

        Constructor<?> constructor = clazz.getConstructor();

        Day dayImpl = (Day) constructor.newInstance();

        Matcher matcher = CLASS_NAME_PATTERN.matcher(dayImpl.getClass().getSimpleName());
        if (!matcher.find()) {
            throw new IllegalStateException("Class name does not match pattern: " + dayImpl.getClass().getSimpleName());
        }
        String dayStr = matcher.group(1);

        dayImpl.run(Integer.parseInt(dayStr));
    }
}
