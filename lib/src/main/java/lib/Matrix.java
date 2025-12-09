package lib;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Matrix {
    /**
     * Creates a char matrix of the input. Every line is assumed to have the same length.
     */
    public static char[][] matrix(List<String> input) {
        int numberOfRows = input.size();
        int numberOfColumns = input.getFirst().length();

        char[][] arr = new char[numberOfRows][numberOfColumns];

        for (int i = 0; i < input.size(); i++) {
            arr[i] = input.get(i).toCharArray();
        }

        return arr;
    }

    /**
     * Creates an int matrix of the input. Every line of the input is assumed
     * to have the same length.
     */
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

    public static char[][] matrix(int rows, int columns, char filler) {
        char[][] arr = new char[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                arr[i][j] = filler;
            }
        }

        return arr;
    }

    public static Object[][] objectMatrix(int rows, int columns, Object filler) {
        Object[][] arr = new Object[rows][columns];

        for (int i = 0; i < rows; i++) {
            arr[i] = new Object[columns];
            for (int j = 0; j < columns; j++) {
                arr[i][j] = filler;
            }
        }

        return arr;
    }

    /**
     * Creates a char matrix from the input that has an extra row/column on all
     * sides. Every line of the input is assumed to have the same length.
     */
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

    public static void fill(Object[][] matrix, Object filler) {
        for (Object[] objects : matrix) {
            Arrays.fill(objects, filler);
        }
    }

    public static Set<Coordinate> toCoordinates(char[][] matrix) {
        Set<Coordinate> coordinates = new HashSet<>(matrix.length * matrix[0].length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                coordinates.add(new Coordinate(i, j));
            }
        }
        return coordinates;
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

    public static Set<Coordinate> findChars(char[][] haystack, char needle) {
        Set<Coordinate> result = new HashSet<>();
        for (int row = 0; row < haystack.length; row++) {
            for (int column = 0; column < haystack[row].length; column++) {
                if (haystack[row][column] == needle) {
                    result.add(new Coordinate(row, column));
                }
            }
        }

        return Set.copyOf(result);
    }

    public static Set<Coordinate> findInts(int[][] haystack, int needle) {
        Set<Coordinate> result = new HashSet<>();
        for (int row = 0; row < haystack.length; row++) {
            for (int column = 0; column < haystack[row].length; column++) {
                if (haystack[row][column] == needle) {
                    result.add(new Coordinate(row, column));
                }
            }
        }

        return Set.copyOf(result);
    }

    /** @return the index of the column where needle is found */
    public static int findChar(char[] haystack, char needle) {
        for (int column = 0; column < haystack.length; column++) {
            if (haystack[column] == needle) {
                return column;
            }
        }

        throw new IllegalStateException("No " + needle + " found in matrix");
    }

    public static Set<Integer> findChars(char[] haystack, char needle) {
        Set<Integer> result = new HashSet<>();
        for (int column = 0; column < haystack.length; column++) {
            if (haystack[column] == needle) {
                result.add(column);
            }
        }

        return result;
    }

    public static boolean findRight(String needle, char[][] matrix, int i, int j) {
        if (matrix[i].length <= j + (needle.length() - 1)) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i][j + n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findLeft(String needle, char[][] matrix, int i, int j) {
        if (j < needle.length() - 1) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i][j - n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findUp(String needle, char[][] matrix, int i, int j) {
        if (i < needle.length() - 1) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i - n][j] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findDown(String needle, char[][] matrix, int i, int j) {
        if (matrix.length <= i + (needle.length() - 1)) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i + n][j] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findDiagonalUpLeft(String needle, char[][] matrix, int i, int j) {
        if (i < needle.length() - 1 || j < needle.length() - 1) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i - n][j - n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findDiagonalUpRight(String needle, char[][] matrix, int i, int j) {
        if (i < needle.length() - 1 || matrix[i].length <= j + (needle.length() - 1)) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i - n][j + n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findDiagonalDownLeft(String needle, char[][] matrix, int i, int j) {
        if (matrix.length <= i + (needle.length() - 1) || j < needle.length() - 1) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i + n][j - n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static boolean findDiagonalDownRight(String needle, char[][] matrix, int i, int j) {
        if (matrix.length <= i + (needle.length() - 1) || matrix[i].length <= j + (needle.length() - 1)) {
            return false;
        }

        for (int n = 0; n < needle.length(); n++) {
            if (matrix[i + n][j + n] != needle.charAt(n)) {
                return false;
            }
        }

        return true;
    }

    public static char[][] deepClone(char[][] input) {
        char[][] result = new char[input.length][];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].clone();
        }
        return result;
    }

    public static int[][] deepClone(int[][] input) {
        int[][] result = new int[input.length][];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i].clone();
        }
        return result;
    }

    public static char[][] toCharArray(int[][] matrix) {
        char[][] result = new char[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            result[i] = new char[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                result[i][j] = Character.forDigit(matrix[i][j], 10);
            }
        }
        return result;
    }

    public static void print(PrintStream s, char[][]... matrices) {
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

    public static void print(PrintStream s, int[][] matrix) {
        for (int[] row : matrix) {
            for (int column : row) {
                s.print(column);
            }
            s.println();
        }
    }

    public static void print(PrintStream s, Object[][] matrix) {
        for (Object[] row : matrix) {
            for (Object column : row) {
                s.print(column);
            }
            s.println();
        }
    }

    public static void printWithoutPadding(char[][] matrix) {
        for (int row = 1; row < matrix.length - 1; row++) {
            for (int column = 1; column < matrix[row].length - 1; column++) {
                System.out.print(matrix[row][column]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public static char[][] rotateRight(char[][] matrix, int n) {
        if (n % 4 == 0) {
            return matrix;
        }

        for (int count = 0; count < (n % 4); count++) {
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

        return matrix;
    }

    public static int manhattanDistance(int row1, int column1,
                                        int row2, int column2) {
        return Math.abs(row1 - row2) +
                Math.abs(column1 - column2);
    }

    public static int manhattanDistance(Coordinate coordinate1,
                                        Coordinate coordinate2) {
        return Math.abs(coordinate1.row() - coordinate2.row()) +
                Math.abs(coordinate1.column() - coordinate2.column());
    }

    public static long area(Coordinate coordinate1, Coordinate coordinate2) {
        long width;
        if (coordinate1.column() > coordinate2.column()) {
            width = coordinate1.column() - coordinate2.column() + 1;
        } else {
            width = coordinate2.column() - coordinate1.column() + 1;
        }

        long height;
        if (coordinate1.row() > coordinate2.row()) {
            height = coordinate1.row() - coordinate2.row() + 1;
        } else {
            height = coordinate2.row() - coordinate1.row() + 1;
        }

        return width * height;
    }
}
