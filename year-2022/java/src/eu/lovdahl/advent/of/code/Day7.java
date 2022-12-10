package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NotNull
public class Day7 {

    public static void run() throws IOException {
        List<String> input = Common.readInputLines( 7 );

        DirectoryEntry root = new DirectoryEntry(
                "root",
                null
        );

        DirectoryEntry currentDirectory = null;

        for ( String line : input ) {
            if ( line.startsWith( "$ " ) ) {
                if ( "$ ls".equals( line ) ) {
                    continue;
                }
                else if ( "$ cd ..".equals( line ) ) {
                    if ( currentDirectory.parent() == null ) {
                        throw new RuntimeException( "No parent when 'cd ..' given" );
                    }

                    currentDirectory = currentDirectory.parent();
                }
                else if ( "$ cd /".equals( line ) ) {
                    currentDirectory = root;
                }
                else if ( line.startsWith( "$ cd " ) ) {
                    String[] split = line.split( "\\$ cd " );
                    String targetDirectoryName = split[1];

                    currentDirectory = currentDirectory.directoryEntries().stream()
                            .filter( d -> d.name().equals( targetDirectoryName ) )
                            .findFirst()
                            .orElseThrow();
                }
            }
            else if ( line.startsWith( "dir " ) ) {
                String[] split = line.split( "dir " );
                String directoryName = split[1];

                DirectoryEntry newDirectory = new DirectoryEntry( directoryName, currentDirectory );
                currentDirectory.directoryEntries().add( newDirectory );
            }
            else {
                String[] split = line.split( " " );

                int fileSize = Integer.parseInt( split[0] );
                String fileName = split[1];

                currentDirectory.addFile(
                        fileName,
                        fileSize
                );
            }
        }

        long sum = root.getAllSubdirectories().stream()
                .filter( e -> e.directorySize().get() <= 100_000 )
                .mapToLong( e -> e.directorySize().get() )
                .sum();

        System.out.println( "Part 1 sum: " + sum );

        long availableSpace = 70_000_000;
        long neededSpace = 30_000_000;
        long spaceToFree = root.directorySize().get() - ( availableSpace - neededSpace );

        Optional<DirectoryEntry> directoryToRemove = root.getAllSubdirectories().stream()
                .sorted( Comparator.comparing( e -> e.directorySize().get() ) )
                .filter( e -> e.directorySize().get() > spaceToFree )
                .findFirst();

        System.out.println( "Part 2 size: " + directoryToRemove.get().directorySize().get() );
    }

    record DirectoryEntry(
            String name,
            @Nullable DirectoryEntry parent,
            Collection<DirectoryEntry> directoryEntries,
            Collection<FileEntry> fileEntries,
            AtomicLong directorySize
    ) {
        DirectoryEntry( String name, @Nullable DirectoryEntry parent ) {
            this( name, parent, new ArrayList<>(), new ArrayList<>(), new AtomicLong() );
        }

        void addFile( String fileName, int fileSize ) {
            fileEntries.add( new FileEntry( fileName, fileSize ) );
            incrementSize( fileSize );
        }

        private void incrementSize( int increment ) {
            directorySize.addAndGet( increment );

            if ( parent != null ) {
                parent.incrementSize( increment );
            }
        }

        Collection<DirectoryEntry> getAllSubdirectories() {
            var result = new ArrayList<DirectoryEntry>();
            result.addAll( directoryEntries );

            for ( DirectoryEntry directoryEntry : directoryEntries ) {
                result.addAll( directoryEntry.getAllSubdirectories() );
            }

            return result;
        }

        @Override
        public String toString() {
            return "DirectoryEntry{" +
                    "name='" + name + '\'' +
                    ", hasParent=" + ( parent != null ) +
                    ", directoryEntries.size()=" + directoryEntries.size() +
                    ", fileEntries=" + fileEntries +
                    ", directorySize=" + directorySize +
                    '}';
        }
    }

    record FileEntry(
            String name,
            int size
    ) {
    }
}
