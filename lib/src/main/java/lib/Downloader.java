package lib;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class Downloader {

    public static void download(int year, int day, Path outputFile, String token) throws IOException, InterruptedException {
        Files.createDirectories(outputFile.getParent());

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://adventofcode.com/" + year + "/day/" + day + "/input"))
                .header("Cookie", "session=" + token)
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
            HttpResponse<String> response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new IllegalStateException("Failed to fetch input for " + year + " day " + day + ": HTTP " + response.statusCode() + ": " + response.body());
            }

            Files.write(outputFile, response.body().getBytes());
        }
    }
}
