package year2023;

import com.google.common.base.Stopwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Common {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    private static final AtomicReference<Stopwatch> PART1_TIMING = new AtomicReference<>();
    private static final AtomicReference<Stopwatch> PART2_TIMING = new AtomicReference<>();

    public static Stream<String> readInputLinesForDay(int day) throws IOException {
        Path path = Path.of("year-2023/" + day + "/input");

        if (!path.toFile().exists()) {
            path = Path.of(day + "/input");
        }

        return Files.lines(path);
    }

    public static List<Integer> ints(String input) {
        return Arrays.stream(SPACE_PATTERN.split(input.trim()))
                .map(Integer::parseInt)
                .toList();
    }

    public static List<Long> longs(String input) {
        return Arrays.stream(SPACE_PATTERN.split(input.trim()))
                .map(Long::parseLong)
                .toList();
    }

    public static void startPart1() {
        PART1_TIMING.set(Stopwatch.createStarted());
    }

    public static void startPart2() {
        PART2_TIMING.set(Stopwatch.createStarted());
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void printTime1(String mark) {
        System.out.printf("Elapsed time %s: %s%n", mark, PART1_TIMING.get());
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void printTime2(String mark) {
        System.out.printf("Elapsed time %s: %s%n", mark, PART2_TIMING.get());
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    public static void result(int part, Object result) {
        Stopwatch stopwatch = switch (part) {
            case 1 -> PART1_TIMING.get().stop();
            case 2 -> PART2_TIMING.get().stop();
            default -> throw new IllegalArgumentException("Unknown part: " + part);
        };

        System.out.printf("""
                ======================================================================
                 Part    %d
                 Time    %s
                 Result  %s
                ======================================================================
                %n""", part, stopwatch, result);
    }

    public static long lcm(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return 0;
        } else {
            long gcd = gcd(number1, number2);
            return Math.abs(number1 * number2) / gcd;
        }
    }

    public static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    public static char[][] matrix(List<String> input) {
        int numberOfRows = input.size();
        int numberOfColumns = input.getFirst().length();

        char[][] arr = new char[numberOfRows][numberOfColumns];

        for (int i = 0; i < input.size(); i++) {
            arr[i] = input.get(i).toCharArray();
        }

        return arr;
    }

    public static char[][] paddedMatrix(List<String> input, char fillerCharacter) {
        int numberOfRows = input.size() + 2;
        int numberOfColumns = input.getFirst().length() + 2;

        char[][] arr = new char[numberOfRows][numberOfColumns];
        Arrays.fill(arr[0], fillerCharacter);
        Arrays.fill(arr[numberOfRows - 1], fillerCharacter);

        for (char[] line : arr) {
            line[0] = fillerCharacter;
            line[numberOfColumns - 1] = fillerCharacter;
        }

        for (int i = 0; i < input.size(); i++) {
            char[] sourceArray = input.get(i).toCharArray();
            System.arraycopy(
                    sourceArray,
                    0,
                    arr[i + 1],
                    1,
                    sourceArray.length
            );
        }

        return arr;
    }

    static <T> List<List<T>> permutations(List<List<T>> input) {
        List<List<T>> permutations = new ArrayList<>();
        int length = input.size();

        int carry;
        int[] indices = new int[length];

        do {
            List<T> instance = new ArrayList<>();
            for (int i = 0; i < indices.length; i++) {
                int index = indices[i];
                instance.add(input.get(i).get(index));
            }
            permutations.add(instance);

            carry = 1;
            for (int i = indices.length - 1; i >= 0; i--) {
                if (carry == 0) {
                    break;
                }

                indices[i] += carry;
                carry = 0;

                if (indices[i] == input.get(i).size()) {
                    carry = 1;
                    indices[i] = 0;
                }
            }
        } while (carry != 1);

        return permutations;
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    static void print(char[][] matrix) {
        for (char[] chars : matrix) {
            for (char ch : chars) {
                System.out.print(ch);
            }
            System.out.println();
        }
        System.out.println();
    }

    static final void swap(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    static final void swap(char[] src, char[] dst, int srcIndex, int dstIndex) {
        char t = src[srcIndex];
        src[srcIndex] = dst[dstIndex];
        dst[dstIndex] = t;
    }

    static void rotateRight(char[][] matrix) {
        // determines the transpose of the matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i; j < matrix.length; j++) {
                char temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // then we reverse the elements of each row
        for (int i = 0; i < matrix.length; i++) {
            // logic to reverse each row i.e. 1D Array
            int low = 0;
            int high = matrix.length - 1;

            while (low < high) {
                char temp = matrix[i][low];
                matrix[i][low] = matrix[i][high];
                matrix[i][high] = temp;
                low++;
                high--;
            }
        }
    }
}
