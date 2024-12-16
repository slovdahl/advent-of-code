package lib;

@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @param w the fourth function argument
     * @return the function result
     */
    R apply(T t, U u, V v, W w);
}
