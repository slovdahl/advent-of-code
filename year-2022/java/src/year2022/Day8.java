package year2022;

import java.io.IOException;
import java.util.List;

import org.jetbrains.annotations.NotNull;

@NotNull
public class Day8 {

    public static void run() throws IOException {
        List<String> input = Common.readInputLinesForDay( 8 );

        int lengthOfLine = input.iterator().next().length();
        int[][] matrix = new int[input.size()][lengthOfLine];

        for ( int i = 0; i < input.size(); i++ ) {
            String line = input.get( i );
            char[] chars = line.toCharArray();

            for ( int j = 0; j < chars.length; j++ ) {
                matrix[i][j] = chars[j] - '0';
            }
        }

        boolean[][] visibleTrees = new boolean[matrix.length][matrix.length];
        for ( int i = 0; i < matrix.length; i++ ) {
            for ( int j = 0; j < matrix.length; j++ ) {
                visibleTrees[i][j] = false;
            }
        }

        int visible = calculateVisible( matrix, visibleTrees );
        visible += calculateVisible( rightRotate( matrix ), rightRotate( visibleTrees ) );
        visible += calculateVisible( rightRotate( matrix ), rightRotate( visibleTrees ) );
        visible += calculateVisible( rightRotate( matrix ), rightRotate( visibleTrees ) );

        System.out.println( "Part 1, visible trees: " + visible );

        int[][] scenicScore = new int[matrix.length][matrix.length];
        int maxScenicScore = -1;

        for ( int i = 0; i < matrix.length; i++ ) {
            for ( int j = 0; j < matrix.length; j++ ) {
                if ( i == 0 || j == 0 ||
                        i == matrix.length - 1 || j == matrix.length - 1 ) {

                    scenicScore[i][j] = 0;
                    continue;
                }

                int treeHeight = matrix[i][j];

                // up
                int visibleUp = 0;
                for ( int upIndex = i - 1; upIndex >= 0; upIndex-- ) {
                    visibleUp++;
                    if ( matrix[upIndex][j] >= treeHeight ) {
                        break;
                    }
                }

                // right
                int visibleRight = 0;
                if ( j < matrix.length - 1 ) {
                    for ( int rightIndex = j + 1; rightIndex < matrix.length; rightIndex++ ) {
                        visibleRight++;
                        if ( matrix[i][rightIndex] >= treeHeight ) {
                            break;
                        }
                    }
                }

                // down
                int visibleDown = 0;
                if ( i < matrix.length - 1 ) {
                    for ( int downIndex = i + 1; downIndex < matrix.length; downIndex++ ) {
                        visibleDown++;
                        if ( matrix[downIndex][j] >= treeHeight ) {
                            break;
                        }
                    }
                }

                // left
                int visibleLeft = 0;
                if ( j > 0 ) {
                    for ( int leftIndex = j - 1; leftIndex >= 0; leftIndex-- ) {
                        visibleLeft++;
                        if ( matrix[i][leftIndex] >= treeHeight ) {
                            break;
                        }
                    }
                }

                int treeScenicScore = visibleUp * visibleRight * visibleDown * visibleLeft;
                scenicScore[i][j] = treeScenicScore;

                if ( treeScenicScore > maxScenicScore ) {
                    maxScenicScore = treeScenicScore;
                }
            }
        }
        System.out.println( "Part 2, maximum scenic score: " + maxScenicScore );
    }

    private static int calculateVisible( int[][] matrix, boolean[][] visibleTrees ) {
        int visible = 0;

        for ( int columnIndex = 0; columnIndex < matrix.length; columnIndex++ ) {
            int maxTreeHeight = -1;
            for ( int rowIndex = 0; rowIndex < matrix.length; rowIndex++ ) {
                if ( maxTreeHeight == -1 ||
                        matrix[rowIndex][columnIndex] > maxTreeHeight ) {

                    if ( !visibleTrees[rowIndex][columnIndex] ) {
                        visible++;
                        visibleTrees[rowIndex][columnIndex] = true;
                    }

                    maxTreeHeight = matrix[rowIndex][columnIndex];
                }
            }
        }

        return visible;
    }

    private static int[][] rightRotate( int[][] matrix ) {
        // determines the transpose of the matrix
        for ( int i = 0; i < matrix.length; i++ ) {
            for ( int j = i; j < matrix.length; j++ ) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // then we reverse the elements of each row
        for ( int i = 0; i < matrix.length; i++ ) {
            // logic to reverse each row i.e. 1D Array
            int low = 0;
            int high = matrix.length - 1;

            while ( low < high ) {
                int temp = matrix[i][low];
                matrix[i][low] = matrix[i][high];
                matrix[i][high] = temp;
                low++;
                high--;
            }
        }

        return matrix;
    }

    private static boolean[][] rightRotate( boolean[][] matrix ) {
        // determines the transpose of the matrix
        for ( int i = 0; i < matrix.length; i++ ) {
            for ( int j = i; j < matrix.length; j++ ) {
                boolean temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // then we reverse the elements of each row
        for ( int i = 0; i < matrix.length; i++ ) {
            // logic to reverse each row i.e. 1D Array
            int low = 0;
            int high = matrix.length - 1;

            while ( low < high ) {
                boolean temp = matrix[i][low];
                matrix[i][low] = matrix[i][high];
                matrix[i][high] = temp;
                low++;
                high--;
            }
        }

        return matrix;
    }
}
