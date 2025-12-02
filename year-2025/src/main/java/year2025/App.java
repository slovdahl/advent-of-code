package year2025;

import lib.Runner;

public class App {

    static void main(String[] args) throws Exception {
        ClassLoader classLoader = App.class.getClassLoader();
        String packageName = "year2025";

        Runner.run(args, classLoader, packageName);
    }
}
