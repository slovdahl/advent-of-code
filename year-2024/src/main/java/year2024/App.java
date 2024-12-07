package year2024;

import lib.Runner;

import java.util.ArrayList;
import java.util.List;

public class App {

    private static final List<? extends Class<?>> DAY_CLASSES;

    static {
        List<Class<?>> classes = new ArrayList<>();
        for (int i = 25; i > 0; i--) {
            try {
                classes.add(Class.forName("year2024.Day" + i));
            } catch (ClassNotFoundException ignored) {
            }
        }
        classes.sort(Runner.DAY_CLASS_COMPARATOR);
        DAY_CLASSES = List.copyOf(classes);
    }

    public static void main(String[] args) throws Exception {
        Runner.run(args, DAY_CLASSES);
    }
}
