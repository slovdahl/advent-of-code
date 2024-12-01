package year2023;

import lib.Day;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day2 extends Day {

    private List<Game> games;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) throws Exception {
        Pattern linePattern = Pattern.compile("^Game (\\d+):(.*)$");
        Pattern playPattern = Pattern.compile("\\s?(\\d+) (blue|green|red)\\s?");

        games = input
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
    }

    @Override
    protected Object part1(Stream<String> input) throws Exception {
        return games.stream()
                .filter(
                        g -> g.plays().stream()
                                .noneMatch(p -> p.red() > 12 || p.green() > 13 || p.blue() > 14)
                )
                .map(Game::id)
                .mapToInt(v -> v)
                .sum();
    }

    @Override
    protected Object part2(Stream<String> input) throws Exception {
        return games.stream()
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
    }

    record Game(int id, List<Play> plays, String rawPlayInput) {
    }

    record Play(int red, int blue, int green) {
    }
}
