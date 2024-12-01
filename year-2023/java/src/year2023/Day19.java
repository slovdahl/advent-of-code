package year2023;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import lib.Day;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

@SuppressWarnings({"unused"})
public class Day19 extends Day {

    private static final Range<Integer> ANY = Range.closed(1, 4000);

    private static final Map<String, ToWorkflowAction> WORKFLOW_ACTION_CACHE = new HashMap<>();

    private List<String> partRatingsStrings;
    private Map<String, Workflow> workflows;

    @Override
    protected Mode mode() {
        return Mode.REAL_INPUT;
    }

    @Override
    protected void prepare(Stream<String> input) {
        Stream<String> sampleInput = """
                px{a<2006:qkq,m>2090:A,rfg}
                pv{a>1716:R,A}
                lnx{m>1548:A,A}
                rfg{s<537:gd,x>2440:R,A}
                qs{s>3448:A,lnx}
                qkq{x<1416:A,crn}
                crn{x>2662:A,R}
                in{s<1351:px,qqz}
                qqz{s>2770:qs,m<1801:hdj,R}
                gd{a>3333:R,R}
                hdj{m>838:A,pv}
                                
                {x=787,m=2655,a=1222,s=2876}
                {x=1679,m=44,a=2067,s=496}
                {x=2036,m=264,a=79,s=2244}
                {x=2461,m=1339,a=466,s=291}
                {x=2127,m=1623,a=2188,s=1013}
                """.lines();

        List<String> workflowStrings = new ArrayList<>();
        partRatingsStrings = new ArrayList<>();

        parseInputIntoWorkflowsAndPartRatings(input.toList(), workflowStrings, partRatingsStrings);

        workflows = parseWorkflows(workflowStrings);
    }

    @Override
    protected Long part1(Stream<String> input) throws IOException {
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
                if (rule.xCondition().contains(state.part().x()) &&
                        rule.mCondition().contains(state.part().m()) &&
                        rule.aCondition().contains(state.part().a()) &&
                        rule.sCondition().contains(state.part().s())) {

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

    @Override
    protected Long part2(Stream<String> input) throws Exception {
        List<RangesPart> acceptedRangesParts = new ArrayList<>();

        RangeState initialState = new RangeState(new RangesPart(ANY, ANY, ANY, ANY), "in");

        Queue<RangeState> states = new ArrayDeque<>();
        states.add(initialState);

        while (!states.isEmpty()) {
            RangeState state = states.poll();
            Workflow workflow = workflows.get(state.workflow());

            for (Rule rule : workflow.rules()) {
                Range<Integer> xIntersection = rule.xCondition().intersection(state.part().x());
                Range<Integer> mIntersection = rule.mCondition().intersection(state.part().m());
                Range<Integer> aIntersection = rule.aCondition().intersection(state.part().a());
                Range<Integer> sIntersection = rule.sCondition().intersection(state.part().s());

                if (xIntersection.isEmpty() || mIntersection.isEmpty() || aIntersection.isEmpty() || sIntersection.isEmpty()) {
                    continue;
                }

                RangesPart newPart = new RangesPart(xIntersection, mIntersection, aIntersection, sIntersection);

                boolean unused = switch (rule.action()) {
                    case StaticAction.ACCEPT -> acceptedRangesParts.add(newPart);
                    case StaticAction.REJECT -> false;
                    case ToWorkflowAction action -> states.add(new RangeState(newPart, action.toWorkflow()));
                };

                Range<Integer> newX = state.part().x();
                Range<Integer> newM = state.part().m();
                Range<Integer> newA = state.part().a();
                Range<Integer> newS = state.part().s();

                if (!rule.xCondition().equals(ANY)) {
                    newX = diff(newX, rule.xCondition());
                } else if (!rule.mCondition().equals(ANY)) {
                    newM = diff(newM, rule.mCondition());
                } else if (!rule.aCondition().equals(ANY)) {
                    newA = diff(newA, rule.aCondition());
                } else if (!rule.sCondition().equals(ANY)) {
                    newS = diff(newS, rule.sCondition());
                }

                state = new RangeState(
                        new RangesPart(
                                newX,
                                newM,
                                newA,
                                newS
                        ),
                        state.workflow()
                );
            }
        }

        List<RangesPart> rangesToProcess;
        List<RangesPart> processed = new ArrayList<>();

        boolean modified;
        do {
            modified = false;
            if (!processed.isEmpty()) {
                rangesToProcess = new ArrayList<>(processed);
                processed.clear();
            } else {
                rangesToProcess = new ArrayList<>(acceptedRangesParts);
            }

            do {
                RangesPart r1 = rangesToProcess.removeFirst();

                Iterator<RangesPart> iterator = rangesToProcess.iterator();
                while (iterator.hasNext()) {
                    RangesPart r2 = iterator.next();

                    if (r1 == r2) {
                        continue;
                    }

                    EnumSet<Type> notEqual = EnumSet.allOf(Type.class);

                    if (r1.x().equals(r2.x())) {
                        notEqual.remove(Type.X);
                    }

                    if (r1.m().equals(r2.m())) {
                        notEqual.remove(Type.M);
                    }

                    if (r1.a().equals(r2.a())) {
                        notEqual.remove(Type.A);
                    }

                    if (r1.s().equals(r2.s())) {
                        notEqual.remove(Type.S);
                    }

                    if (notEqual.size() == 1) {
                        Type part = notEqual.iterator().next();

                        boolean isConnected = switch (part) {
                            case X -> r1.x().isConnected(r2.x());
                            case M -> r1.m().isConnected(r2.m());
                            case A -> r1.a().isConnected(r2.a());
                            case S -> r1.s().isConnected(r2.s());
                        };

                        if (isConnected) {
                            modified = true;
                            r1 = switch (part) {
                                case X -> new RangesPart(r1.x().span(r2.x()), r1.m(), r1.a(), r1.s());
                                case M -> new RangesPart(r1.x(), r1.m().span(r2.m()), r1.a(), r1.s());
                                case A -> new RangesPart(r1.x(), r1.m(), r1.a().span(r2.a()), r1.s());
                                case S -> new RangesPart(r1.x(), r1.m(), r1.a(), r1.s().span(r2.s()));
                            };
                            iterator.remove();
                        }
                    }
                }

                processed.add(r1);
            } while (!rangesToProcess.isEmpty());
        } while (modified);

        return processed.stream()
                .mapToLong(p ->
                        ((long) (ContiguousSet.create(p.x(), DiscreteDomain.integers()).size())) *
                                ((long) (ContiguousSet.create(p.m(), DiscreteDomain.integers()).size())) *
                                ((long) (ContiguousSet.create(p.a(), DiscreteDomain.integers()).size())) *
                                ((long) (ContiguousSet.create(p.s(), DiscreteDomain.integers()).size()))
                )
                .sum(); // Your puzzle answer was 110807725108076
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
                Range<Integer> xCondition = ANY;
                Range<Integer> mCondition = ANY;
                Range<Integer> aCondition = ANY;
                Range<Integer> sCondition = ANY;
                Action action;

                if (ruleString.contains(":")) {
                    String[] conditionAndAction = ruleString.split(":");

                    int test = Integer.parseInt(conditionAndAction[0].substring(2));

                    Range<Integer> range = switch (conditionAndAction[0].charAt(1)) {
                        case '>' -> Range.openClosed(test, 4000);
                        case '<' -> Range.closedOpen(1, test);
                        default -> throw new IllegalStateException();
                    };

                    switch (conditionAndAction[0].charAt(0)) {
                        case 'x':
                            xCondition = range;
                            break;
                        case 'm':
                            mCondition = range;
                            break;
                        case 'a':
                            aCondition = range;
                            break;
                        case 's':
                            sCondition = range;
                            break;
                        default:
                            throw new IllegalStateException();
                    }

                    action = Action.from(conditionAndAction[1]);
                } else {
                    action = Action.from(ruleString);
                }

                rules.add(new Rule(xCondition, mCondition, aCondition, sCondition, action));
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

    private static Range<Integer> diff(Range<Integer> newPartRange, Range<Integer> rule) {
        ImmutableRangeSet<Integer> difference = ImmutableRangeSet.of(newPartRange).difference(ImmutableRangeSet.of(rule));

        if (difference.asRanges().size() != 1) {
            throw new IllegalStateException("Difference: " + difference);
        }

        return difference.span();
    }

    private record Workflow(List<Rule> rules) {
    }

    private record Part(int x, int m, int a, int s) {
    }

    private record RangesPart(Range<Integer> x,
                              Range<Integer> m,
                              Range<Integer> a,
                              Range<Integer> s) {
    }

    private record Rule(Range<Integer> xCondition,
                        Range<Integer> mCondition,
                        Range<Integer> aCondition,
                        Range<Integer> sCondition,
                        Action action) {
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

    private record RangeState(RangesPart part, String workflow) {
        RangeState withNextWorkflow(String workflow) {
            return new RangeState(part, workflow);
        }
    }

    private enum Type {
        X,
        M,
        A,
        S
    }
}
