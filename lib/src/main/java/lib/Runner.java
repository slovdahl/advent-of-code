package lib;

import com.google.common.reflect.ClassPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Runner {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("Day(\\d+)");

    public static void run(String[] args, ClassLoader classLoader, String packageName) throws Exception {
        Comparator<Class<?>> comparator = Comparator
                // Hack to get sorting of Day1, Day2, .., Day10, Day 11 to work as expected
                // without actually comparing the integers.
                .<Class<?>, Integer>comparing(cls -> cls.getSimpleName().length())
                .thenComparing(Class::getName);

        List<? extends Class<?>> dayClasses = ClassPath.from(classLoader).getTopLevelClasses(packageName).stream()
                .map(ClassPath.ClassInfo::load)
                .filter(Day.class::isAssignableFrom)
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .sorted(comparator)
                .toList();

        Class<?> clazz;
        if (args.length > 0) {
            clazz = dayClasses
                    .stream()
                    .filter(cls -> cls.getSimpleName().endsWith("Day" + args[0]))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No such day: " + args[0]));
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
