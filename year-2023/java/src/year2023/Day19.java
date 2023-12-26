package year2023;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@SuppressWarnings({"unused"})
public class Day19 extends Day {

    private static final Predicate<Part> ANY = part -> true;

    private static final Map<String, ToWorkflowAction> WORKFLOW_ACTION_CACHE = new HashMap<>();

    @Override
    Long part1(Stream<String> input) throws IOException {
        List<String> workflowStrings = new ArrayList<>();
        List<String> partRatingsStrings = new ArrayList<>();

        parseInputIntoWorkflowsAndPartRatings(input.toList(), workflowStrings, partRatingsStrings);

        Map<String, Workflow> workflows = parseWorkflows(workflowStrings);
        List<Part> parts = parseParts(partRatingsStrings);

        Queue<State> states = new ArrayDeque<>();
        for (Part part : parts) {
            states.add(new State(part, "in"));
        }

        List<Part> acceptedParts = new ArrayList<>();
        while (!states.isEmpty()) {
            State state = states.poll();
            Workflow workflow = workflows.get(state.workflow());

            for (Rule rule : workflow.rules()) {
                boolean result = rule.condition().test(state.part());
                if (result) {
                    boolean unused = switch (rule.action()) {
                        case StaticAction.REJECT -> false;
                        case StaticAction.ACCEPT -> acceptedParts.add(state.part());
                        case ToWorkflowAction action -> states.add(state.withNextWorkflow(action.toWorkflow()));
                    };

                    break;
                }
            }
        }

        return acceptedParts.stream()
                .mapToLong(p -> p.x() + p.m() + p.a() + p.s())
                .sum(); // Your puzzle answer was 319295
    }

    private static void parseInputIntoWorkflowsAndPartRatings(List<String> inputLines, List<String> workflowStrings, List<String> partRatingsStrings) {
        boolean workflow = true;
        for (String inputLine : inputLines) {
            if (inputLine.isEmpty()) {
                workflow = false;
                continue;
            }

            if (workflow) {
                workflowStrings.add(inputLine);
            } else {
                partRatingsStrings.add(inputLine);
            }
        }
    }

    private static Map<String, Workflow> parseWorkflows(List<String> workflowStrings) {
        Map<String, Workflow> workflows = new HashMap<>();

        for (String workflowString : workflowStrings) {
            int firstCurlyBrace = workflowString.indexOf("{");
            String name = workflowString.substring(0, firstCurlyBrace);
            String rulesString = workflowString.substring(firstCurlyBrace + 1, workflowString.length() - 1);

            List<Rule> rules = new ArrayList<>();
            for (String ruleString : rulesString.split(",")) {
                Predicate<Part> condition;
                Action action;

                if (ruleString.contains(":")) {
                    String[] conditionAndAction = ruleString.split(":");

                    Function<Part, Integer> fn = switch (conditionAndAction[0].charAt(0)) {
                        case 'x' -> Part::x;
                        case 'm' -> Part::m;
                        case 'a' -> Part::a;
                        case 's' -> Part::s;
                        default -> throw new IllegalStateException("Failed to parse rule string: " + ruleString);
                    };

                    int test = Integer.parseInt(conditionAndAction[0].substring(2));

                    condition = switch (conditionAndAction[0].charAt(1)) {
                        case '>' -> p -> fn.apply(p) > test;
                        case '<' -> p -> fn.apply(p) < test;
                        default -> throw new IllegalStateException();
                    };

                    action = Action.from(conditionAndAction[1]);
                } else {
                    condition = ANY;
                    action = Action.from(ruleString);
                }

                rules.add(new Rule(condition, action));

                if (condition == ANY) {
                    break;
                }
            }

            workflows.put(name, new Workflow(List.copyOf(rules)));
        }

        return Map.copyOf(workflows);
    }

    private static List<Part> parseParts(List<String> partRatingsStrings) {
        List<Part> parts = new ArrayList<>();

        for (String partRatingsString : partRatingsStrings) {
            String[] split = partRatingsString.substring(1, partRatingsString.length() - 1).split(",");

            parts.add(new Part(
                    Integer.parseInt(split[0].substring(2)),
                    Integer.parseInt(split[1].substring(2)),
                    Integer.parseInt(split[2].substring(2)),
                    Integer.parseInt(split[3].substring(2))
            ));
        }
        return parts;
    }

    private record Workflow(List<Rule> rules) {
    }

    private record Part(int x, int m, int a, int s) {
    }

    private record Rule(Predicate<Part> condition, Action action) {
    }

    private sealed interface Action {
        static Action from(String action) {
            return switch (action) {
                case "A" -> StaticAction.ACCEPT;
                case "R" -> StaticAction.REJECT;
                default -> WORKFLOW_ACTION_CACHE.computeIfAbsent(action, ToWorkflowAction::new);
            };
        }
    }

    private enum StaticAction implements Action {
        ACCEPT,
        REJECT
    }

    private record ToWorkflowAction(String toWorkflow) implements Action {
    }

    private record State(Part part, String workflow) {
        State withNextWorkflow(String workflow) {
            return new State(part, workflow);
        }
    }
}
