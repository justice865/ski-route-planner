package edu.kit.kastel.model.planning;

import edu.kit.kastel.model.config.Goal;
import edu.kit.kastel.model.config.PreferenceProfile;
import edu.kit.kastel.model.config.Skill;
import edu.kit.kastel.model.config.SkierConfig;
import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.core.SkiArea;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.elements.Slope;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The class that plans a route for a skier in a ski area.
 * @author uqibg
 */
public class RoutePlanner {
    private static final int ZERO = 0;
    private static final int POSITIVE_ONE = 1;
    private static final int NEGATIVE_ONE = -1;
    private static final int INVALID_UTILITY = Integer.MIN_VALUE;

    private RoutePlan bestRoute;
    private Lift activeStartLift;
    private LocalTime activeStartTime;
    private boolean requireTransitEnd;
    private boolean requireSameStartTransitEnd;
    private Node alternativeOrigin;
    private Node forbiddenNextFromOrigin;

    /**
     * Constructor for RoutePlanner.
     */
    public RoutePlanner() {
        this.bestRoute = null;
        this.activeStartLift = null;
        this.activeStartTime = null;
        this.requireTransitEnd = false;
        this.requireSameStartTransitEnd = false;
        this.alternativeOrigin = null;
        this.forbiddenNextFromOrigin = null;
    }

    /**
     * Plans a route for skier.
     * @param area the ski area to plan in
     * @param config the configuration of the skier
     * @param startLift the lift to start from
     * @param startTime the time to start at
     * @param endTime the time to end at
     * @return the planned route, or null if no valid route can be planned
     */
    public RoutePlan plan(SkiArea area, SkierConfig config, Lift startLift,
                          LocalTime startTime, LocalTime endTime) {
        if (area == null || config == null || startLift == null || startTime == null || endTime == null) {
            return null;
        }
        if (!config.isReadyForPlanning() || !startTime.isBefore(endTime)) {
            return null;
        }
        if (!startLift.isTransit()) {
            return null;
        }
        if (area.getNode(startLift.getId()) != startLift) {
            return null;
        }

        this.bestRoute = null;
        this.activeStartLift = startLift;
        this.activeStartTime = startTime;
        this.requireTransitEnd = true;
        this.requireSameStartTransitEnd = true;
        this.alternativeOrigin = null;
        this.forbiddenNextFromOrigin = null;

        LocalTime afterStart = advanceTime(startLift, startTime, config.getSkill());
        if (afterStart == null || !afterStart.isBefore(endTime)) {
            return null;
        }

        List<Node> initialSteps = new ArrayList<>();
        initialSteps.add(startLift);
        search(area, config, startLift, afterStart, endTime, initialSteps);
        return this.bestRoute;
    }

    /**
     * Plans an alternative route for a skier which already has an active route.
     * @param area the ski area to plan in
     * @param config the configuration of the skier
     * @param currentNode the current node the skier is at
     * @param currentTime the current time
     * @param endTime the time to end at
     * @param forbiddenNext the next node in the current active route which is
     *                      to be attempted to deviate from
     * @return the planned alternative route, or null if no valid alternative route can be planned
     */
    public RoutePlan planAlternative(SkiArea area, SkierConfig config,
                                     Node currentNode, LocalTime currentTime,
                                     LocalTime endTime, Node forbiddenNext) {
        if (area == null || config == null || currentNode == null || currentTime == null || endTime == null) {
            return null;
        }
        if (!config.isReadyForPlanning() || !currentTime.isBefore(endTime)) {
            return null;
        }
        if (area.getNode(currentNode.getId()) != currentNode) {
            return null;
        }

        this.bestRoute = null;
        this.activeStartLift = currentNode instanceof Lift lift ? lift : null;
        this.activeStartTime = currentTime;
        this.requireTransitEnd = true;
        this.requireSameStartTransitEnd = false;
        this.alternativeOrigin = currentNode;
        this.forbiddenNextFromOrigin = forbiddenNext;

        search(area, config, currentNode, currentTime, endTime, new ArrayList<>());
        return this.bestRoute;
    }

    private void search(SkiArea area, SkierConfig config, Node currentNode,
                        LocalTime currentTime, LocalTime endTime,
                        List<Node> currentSteps) {
        if (currentNode == null || currentTime == null || endTime == null) {
            return;
        }
        if (!currentTime.isBefore(endTime)) {
            return;
        }

        if (isValidFinishedRoute(currentSteps, currentNode, currentTime, endTime)) {
            RoutePlan candidate = buildRoutePlan(currentSteps, this.activeStartLift, this.activeStartTime, endTime);
            if (compareRoutes(candidate, this.bestRoute, config) > ZERO) {
                this.bestRoute = candidate;
            }
        }

        for (Node next : area.getOutgoing(currentNode)) {
            if (currentNode == this.alternativeOrigin
                    && currentSteps.isEmpty()
                    && this.forbiddenNextFromOrigin != null
                    && next == this.forbiddenNextFromOrigin) {
                continue;
            }
            if (!isStepAllowed(currentNode, next)) {
                continue;
            }

            LocalTime nextTime = advanceTime(next, currentTime, config.getSkill());
            if (nextTime == null || !nextTime.isBefore(endTime)) {
                continue;
            }

            List<Node> nextSteps = new ArrayList<>(currentSteps);
            nextSteps.add(next);
            search(area, config, next, nextTime, endTime, nextSteps);
        }
    }

    private boolean isStepAllowed(Node from, Node to) {
        return from != null && to != null && from.hasOutgoingTo(to);
    }

    private LocalTime advanceTime(Node next, LocalTime currentTime, Skill skill) {
        if (next == null || currentTime == null) {
            return null;
        }

        LocalTime advanced;
        if (next instanceof Lift lift) {
            if (!lift.canBeTakenAt(currentTime)) {
                return null;
            }
            advanced = lift.getArrivalTime(currentTime);
        } else if (next instanceof Slope slope) {
            if (skill == null) {
                return null;
            }
            long travelSeconds = slope.getTravelSeconds(skill);
            if (travelSeconds <= ZERO) {
                return null;
            }
            advanced = currentTime.plusSeconds(travelSeconds);
        } else {
            return null;
        }

        if (!advanced.isAfter(currentTime)) {
            return null;
        }
        return advanced;
    }

    private boolean isValidFinishedRoute(List<Node> steps, Node currentNode, LocalTime currentTime,
                                         LocalTime endTime) {
        if (steps == null || steps.isEmpty() || currentNode == null || currentTime == null || endTime == null) {
            return false;
        }
        if (!currentTime.isBefore(endTime)) {
            return false;
        }
        if (!this.requireTransitEnd) {
            return true;
        }
        if (this.requireSameStartTransitEnd) {
            return this.activeStartLift != null && hasOutgoingToTransitLift(currentNode, this.activeStartLift);
        }
        return hasOutgoingTransitLift(currentNode);
    }

    private boolean hasOutgoingToTransitLift(Node node, Lift requiredTransitLift) {
        if (node == null || requiredTransitLift == null) {
            return false;
        }
        for (Node outgoing : node.getOutgoing()) {
            if (outgoing instanceof Lift lift
                    && lift.isTransit()
                    && requiredTransitLift.getId().equals(lift.getId())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasOutgoingTransitLift(Node node) {
        for (Node outgoing : node.getOutgoing()) {
            if (outgoing instanceof Lift lift && lift.isTransit()) {
                return true;
            }
        }
        return false;
    }

    private RoutePlan buildRoutePlan(List<Node> steps, Lift startLift,
                                     LocalTime startTime, LocalTime endTime) {
        return new RoutePlan(new ArrayList<>(steps), startLift, startTime, endTime);
    }

    private int evaluateUtility(RoutePlan route, Goal goal) {
        if (route == null || goal == null) {
            return INVALID_UTILITY;
        }
        return switch (goal) {
            case ALTITUDE -> route.getTotalElevation();
            case DISTANCE -> route.getTotalDistance();
            case NUMBER -> route.getSlopeCount();
            case UNIQUE -> route.getUniqueSlopeCount();
        };
    }

    private int evaluatePreferenceScore(RoutePlan route, PreferenceProfile preferences) {
        if (route == null || preferences == null) {
            return ZERO;
        }
        return route.getPreferenceScore(preferences);
    }

    private int compareRoutes(RoutePlan first, RoutePlan second, SkierConfig config) {
        if (first == null) {
            return NEGATIVE_ONE;
        }
        if (second == null) {
            return POSITIVE_ONE;
        }

        int utilityCmp = Integer.compare(
                evaluateUtility(first, config.getGoal()),
                evaluateUtility(second, config.getGoal())
        );
        if (utilityCmp != ZERO) {
            return utilityCmp;
        }

        int preferenceCmp = Integer.compare(
                evaluatePreferenceScore(first, config.getPreferences()),
                evaluatePreferenceScore(second, config.getPreferences())
        );
        if (preferenceCmp != ZERO) {
            return preferenceCmp;
        }

        return -compareLexicographically(first, second);
    }

    private int compareLexicographically(RoutePlan first, RoutePlan second) {
        List<Node> firstSteps = first.steps();
        List<Node> secondSteps = second.steps();

        int minLength = Math.min(firstSteps.size(), secondSteps.size());
        for (int i = ZERO; i < minLength; i++) {
            int cmp = firstSteps.get(i).getId().compareTo(secondSteps.get(i).getId());
            if (cmp != ZERO) {
                return cmp;
            }
        }
        return Integer.compare(firstSteps.size(), secondSteps.size());
    }
}

