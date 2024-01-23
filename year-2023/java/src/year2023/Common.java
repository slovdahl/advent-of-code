package year2023;

import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import year2023.tools.Coordinate;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Common {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    public static Stream<String> readInputLinesForDay(int day) throws IOException {
        Path path = Path.of("year-2023/input/" + day + "/input");

        if (!path.toFile().exists()) {
            path = Path.of("input/" + day + "/input");
            if (!path.toFile().exists()) {
                // TODO: fetch from adventofcode.com
                throw new NoSuchFileException(path.toString());
            }
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

    public static Stream<String> splitOnComma(String input) {
        return Arrays.stream(input.split(","));
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

    public static int[][] intMatrix(List<String> input) {
        int numberOfRows = input.size();
        int numberOfColumns = input.getFirst().length();

        int[][] arr = new int[numberOfRows][numberOfColumns];

        for (int i = 0; i < input.size(); i++) {
            int j = 0;
            arr[i] = new int[input.get(i).length()];
            for (char ch : input.get(i).toCharArray()) {
                arr[i][j++] = Character.getNumericValue(ch);
            }
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

    public static Coordinate findChar(char[][] haystack, char needle) {
        for (int row = 0; row < haystack.length; row++) {
            for (int column = 0; column < haystack[row].length; column++) {
                if (haystack[row][column] == needle) {
                    return new Coordinate(row, column);
                }
            }
        }

        throw new IllegalStateException("No " + needle + " found in matrix");
    }

    public static char[][] deepClone(char[][] input) {
        char[][] result = new char[input.length][];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].clone();
        }
        return result;
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

    static void print(PrintStream s, char[][]... matrices) {
        if (matrices.length == 0) {
            return;
        }

        for (int row = 0; row < matrices[0].length; row++) {
            for (char[][] matrix : matrices) {
                for (int column = 0; column < matrix[row].length; column++) {
                    s.print(matrix[row][column]);
                }
            }
            s.println();
        }
    }

    static void printWithoutPadding(char[][] matrix) {
        for (int row = 1; row < matrix.length - 1; row++) {
            for (int column = 1; column < matrix[row].length - 1; column++) {
                System.out.print(matrix[row][column]);
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

    public static int manhattanDistance(int row1, int column1,
                                        int row2, int column2) {
        return Math.abs(row1 - row2) +
                Math.abs(column1 - column2);
    }

    public static void visualizeAsDot(PrintStream s, Graph<String> graph) {
        s.println("graph aoc {");

        for (EndpointPair<String> edge : graph.edges()) {
            s.println("  " + edge.nodeU() + " -- " + edge.nodeV());
        }

        s.println("}");
    }
}
