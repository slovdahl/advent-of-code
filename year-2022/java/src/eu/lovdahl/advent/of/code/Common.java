package eu.lovdahl.advent.of.code;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Common {

    public static List<String> readInputLines( int day ) throws IOException {
        return Files.readAllLines( Path.of( "year-2022/" + day + "/input" ) );
    }
}
