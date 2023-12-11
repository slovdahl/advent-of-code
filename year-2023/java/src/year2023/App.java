package year2023;

import com.google.common.reflect.ClassPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;

public class App {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("Day(\\d+)");

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void main(String[] args) throws Exception {
        List<? extends Class<?>> dayClasses = ClassPath.from(App.class.getClassLoader()).getTopLevelClasses("year2023").stream()
                .map(ClassPath.ClassInfo::load)
                .filter(Day.class::isAssignableFrom)
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .sorted(comparing(Class::getName))
                .toList();

        Class<?> latestClass = dayClasses.getLast();

        Constructor<?> constructor = latestClass.getConstructor();

        Day dayImpl = (Day)constructor.newInstance();

        Matcher matcher = CLASS_NAME_PATTERN.matcher(dayImpl.getClass().getSimpleName());
        matcher.find();
        String dayStr = matcher.group(1);

        int day = Integer.parseInt(dayStr);

        System.out.println("Day " + day);

        dayImpl.runPart1(Common.readInputLinesForDay(day));
        dayImpl.runPart2(Common.readInputLinesForDay(day));
    }
}
