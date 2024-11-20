package year2023;

import lib.Runner;

public class App {

    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = App.class.getClassLoader();
        String packageName = "year2023";

        Runner.run(args, classLoader, packageName);
    }
}
