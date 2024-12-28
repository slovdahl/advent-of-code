package year2024;

import lib.Day;
import lib.Matrix;
import lib.Parse;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day25 extends Day {

    private List<Lock> locks;
    private List<Key> keys;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        List<List<String>> sections = Parse.sections(input);

        locks = sections.stream()
                .filter(c -> c.getFirst().equals("#####") && c.getLast().equals("....."))
                .map(c -> c.subList(1, c.size() - 1))
                .map(Matrix::matrix)
                .map(matrix -> Matrix.rotateRight(matrix, 1))
                .map(matrix -> new Lock(
                        Matrix.findChars(matrix[0], '#').size(),
                        Matrix.findChars(matrix[1], '#').size(),
                        Matrix.findChars(matrix[2], '#').size(),
                        Matrix.findChars(matrix[3], '#').size(),
                        Matrix.findChars(matrix[4], '#').size()
                ))
                .toList();

        keys = sections.stream()
                .filter(c -> c.getFirst().equals(".....") && c.getLast().equals("#####"))
                .map(c -> c.subList(1, c.size() - 1))
                .map(Matrix::matrix)
                .map(matrix -> Matrix.rotateRight(matrix, 1))
                .map(matrix -> new Key(
                        Matrix.findChars(matrix[0], '#').size(),
                        Matrix.findChars(matrix[1], '#').size(),
                        Matrix.findChars(matrix[2], '#').size(),
                        Matrix.findChars(matrix[3], '#').size(),
                        Matrix.findChars(matrix[4], '#').size()
                ))
                .toList();
    }

    @Override
    protected Object part1(Stream<String> input) {
        return locks.stream()
                .flatMap(lock ->
                        keys.stream()
                                .filter(lock::fits)
                )
                .count();
    }

    private record Lock(int h1, int h2, int h3, int h4, int h5) {
        boolean fits(Key key) {
            return h1 + key.h1 <= 5 &&
                    h2 + key.h2 <= 5 &&
                    h3 + key.h3 <= 5 &&
                    h4 + key.h4 <= 5 &&
                    h5 + key.h5 <= 5;
        }
    }

    private record Key(int h1, int h2, int h3, int h4, int h5) {
    }
}
