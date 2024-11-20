package year2022;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.NotNull;

@NotNull
public class Day9 {

    private static boolean DEBUG = System.getProperty( "debug" ) != null;

    public static void run() throws IOException {
        List<String> input = Common.readInputLinesForDay( 9 );

        Matrix matrix = new Matrix();
        matrix.visualize( "Starting point" );

        for ( String line : input ) {
            String[] split = line.split( " " );

            Direction direction = switch ( split[0] ) {
                case "U" -> Direction.UP;
                case "D" -> Direction.DOWN;
                case "R" -> Direction.RIGHT;
                case "L" -> Direction.LEFT;
                default -> throw new RuntimeException( "Unexpected input" );
            };

            int steps = Integer.parseInt( split[1] );

            matrix.moveHead( direction, steps );
            matrix.visualize( line );

            if ( !matrix.tail.get().isAdjacentToOrOverlapping( matrix.head.get() ) ) {
                throw new RuntimeException( "Invalid matrix state: " + matrix );
            }
        }

        System.out.println( "Part 1, number of visited tail coordinates: " + matrix.tailVisitedCoordinates.size() );
    }

    record Matrix(
            AtomicReference<Coordinate> head,
            AtomicReference<Coordinate> tail,
            Set<Coordinate> tailVisitedCoordinates
    ) {
        Matrix() {
            this(
                    new AtomicReference<>( new Coordinate( 0, 0 ) ),
                    new AtomicReference<>( new Coordinate( 0, 0 ) ),
                    new HashSet<>( Arrays.asList( new Coordinate( 0, 0 ) ) )
            );
        }

        void moveHead( Direction direction, int steps ) {
            Coordinate oldHead = head.get();
            Coordinate oldTail = tail.get();

            Coordinate newHead = oldHead.move( direction, steps );

            head.set( newHead );

            if ( oldTail.isAdjacentToOrOverlapping( newHead ) ) {
                return;
            }

            Coordinate updatedTail = oldTail;

            if ( newHead.x != oldTail.x && newHead.y != oldTail.y ) {
                // one diagonal move needed first

                Coordinate newTail;
                if ( newHead.x - oldTail.x == 1 ) {
                    /*
                    ...H..
                    ......
                    ......
                    ..T...
                     */

                    if ( newHead.y > oldTail.y ) {
                        newTail = oldTail
                                .move( Direction.RIGHT )
                                .move( Direction.UP );
                    }
                    else {
                        newTail = oldTail
                                .move( Direction.RIGHT )
                                .move( Direction.DOWN );
                    }
                }
                else if ( newHead.x - oldTail.x == -1 ) {
                    /*
                    ...T..
                    ......
                    ......
                    ..H...
                     */

                    if ( newHead.y > oldTail.y ) {
                        newTail = oldTail
                                .move( Direction.LEFT )
                                .move( Direction.UP );
                    }
                    else {
                        newTail = oldTail
                                .move( Direction.LEFT )
                                .move( Direction.DOWN );
                    }
                }
                else if ( newHead.y - oldTail.y == 1 ) {
                    /*
                    ......
                    ....H.
                    .T....
                    ......
                     */

                    if ( newHead.x > oldTail.x ) {
                        newTail = oldTail
                                .move( Direction.UP )
                                .move( Direction.RIGHT );
                    }
                    else {
                        newTail = oldTail
                                .move( Direction.UP )
                                .move( Direction.LEFT );
                    }
                }
                else if ( newHead.y - oldTail.y == -1 ) {
                    /*
                    ......
                    ....T.
                    .H....
                    ......
                     */

                    if ( newHead.x > oldTail.x ) {
                        newTail = oldTail
                                .move( Direction.DOWN )
                                .move( Direction.RIGHT );
                    }
                    else {
                        newTail = oldTail
                                .move( Direction.DOWN )
                                .move( Direction.LEFT );
                    }
                }
                else {
                    throw new RuntimeException( "Unexpected state" );
                }

                tailVisitedCoordinates.add( newTail );
                tail.set( newTail );
                updatedTail = newTail;
            }

            if ( ( direction.isUpOrDown() && newHead.x == updatedTail.x ) ||
                    direction.isRightOrLeft() && newHead.y == updatedTail.y ) {

                while ( !updatedTail.isAdjacentToOrOverlapping( newHead ) ) {
                    updatedTail = updatedTail.move( direction );
                    tailVisitedCoordinates.add( updatedTail );
                }

                tail.set( updatedTail );
            }
        }

        void visualize( String input ) {
            if ( !DEBUG ) {
                return;
            }

            System.out.println( input + " (" + this + "):" );
            int minX = Math.min( Math.min( head.get().x, tail.get().x ), 0 );
            int minY = Math.min( Math.min( head.get().y, tail.get().y ), 0 );
            int maxX = Math.max( Math.max( head.get().x, tail.get().x ), 5 );
            int maxY = Math.max( Math.max( head.get().y, tail.get().y ), 5 );

            for ( int y = maxY; y >= minY; y-- ) {
                for ( int x = minX; x <= maxX; x++ ) {
                    if ( head.get().x == x && head.get().y == y ) {
                        System.out.print( "H" );
                        continue;
                    }

                    if ( tail.get().x == x && tail.get().y == y ) {
                        System.out.print( "T" );
                        continue;
                    }

                    System.out.print( "." );
                }
                System.out.println();
            }

            System.out.println();
        }
    }

    record Coordinate( int x, int y ) {
        boolean isAdjacentToOrOverlapping( Coordinate coordinate ) {
            if ( coordinate.equals( this ) ) {
                return true;
            }

            if ( Math.abs( x - coordinate.x ) <= 1 && Math.abs( y - coordinate.y ) <= 1 ) {
                return true;
            }

            return false;
        }

        Coordinate move( Direction direction ) {
            return move( direction, 1 );
        }

        Coordinate move( Direction direction, int steps ) {
            return switch ( direction ) {
                case UP -> new Coordinate( x, y + steps );
                case DOWN -> new Coordinate( x, y - steps );
                case RIGHT -> new Coordinate( x + steps, y );
                case LEFT -> new Coordinate( x - steps, y );
            };
        }
    }

    enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT;

        boolean isUpOrDown() {
            return this == UP || this == DOWN;
        }

        boolean isRightOrLeft() {
            return this == RIGHT || this == LEFT;
        }
    }
}
