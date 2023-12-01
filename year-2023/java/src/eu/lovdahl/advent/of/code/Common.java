package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Common {

    public static Stream<String> readInputLinesForDay(int day ) throws IOException {
        Path path = Path.of( "year-2023/" + day + "/input" );

        if ( !path.toFile().exists() ) {
            path = Path.of( day + "/input" );
        }

        return Files.lines( path );
    }
}
