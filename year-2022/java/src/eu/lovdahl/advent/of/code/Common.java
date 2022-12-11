package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Common {

    public static List<String> readInputLinesForDay( int day ) throws IOException {
        Path path = Path.of( "year-2022/" + day + "/input" );

        if ( !path.toFile().exists() ) {
            path = Path.of( day + "/input" );
        }

        return Files.readAllLines( path );
    }
}
