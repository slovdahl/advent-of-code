package year2023;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static year2023.Common.readInputLinesForDay;
import static year2023.Common.result;
import static year2023.Common.startPart1;
import static year2023.Common.startPart2;

class Day2 {

    public static void main(String[] args) throws IOException {
        startPart1();
        startPart2();

        Pattern linePattern = Pattern.compile("^Game (\\d+):(.*)$");
        Pattern playPattern = Pattern.compile("\\s?(\\d+) (blue|green|red)\\s?");

        var games = readInputLinesForDay(2)
                .map(linePattern::matcher)
                .filter(Matcher::matches)
                .map(m -> new Game(Integer.parseInt(m.group(1)), List.of(), m.group(2)))
                .map(g -> {
                    List<Play> plays = new ArrayList<>();
                    for (String rawPlay : g.rawPlayInput.split(";")) {
                        int red = 0;
                        int blue = 0;
                        int green = 0;

                        for (String playElement : rawPlay.split(",")) {
                            Matcher matcher = playPattern.matcher(playElement);
                            if (!matcher.matches()) {
                                continue;
                            }

                            switch (matcher.group(2)) {
                                case "red":
                                    red = Integer.parseInt(matcher.group(1));
                                    break;
                                case "blue":
                                    blue = Integer.parseInt(matcher.group(1));
                                    break;
                                case "green":
                                    green = Integer.parseInt(matcher.group(1));
                                    break;
                            }
                        }

                        plays.add(new Play(red, blue, green));
                    }

                    return new Game(g.id, plays, "");
                })
                .toList();

        part1(games);
        part2(games);
    }

    public static void part1(List<Game> games) {
        var result = games.stream()
                .filter(
                        g -> g.plays().stream()
                                .noneMatch(p -> p.red() > 12 || p.green() > 13 || p.blue() > 14)
                )
                .map(Game::id)
                .mapToInt(v -> v)
                .sum();

        result(1, result);
    }

    public static void part2(List<Game> games) {
        var result = games.stream()
                .map(
                        g -> g.plays().stream()
                                .reduce((play, play2) -> new Play(
                                        Math.max(play.red(), play2.red()),
                                        Math.max(play.blue(), play2.blue()),
                                        Math.max(play.green(), play2.green())
                                ))
                                .orElseThrow()
                )
                .map(play -> play.red() * play.blue() * play.green())
                .mapToInt(v -> v)
                .sum();

        result(2, result);
    }

    record Game(int id, List<Play> plays, String rawPlayInput) {
    }

    record Play(int red, int blue, int green) {
    }
}
