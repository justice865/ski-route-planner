package edu.kit.kastel.model.planning;

import edu.kit.kastel.model.config.PreferenceProfile;
import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.elements.Slope;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The RoutePlan record represents a planned route with a list of steps for skier.
 * @param steps the list of steps in the planned route, which can be either lifts or slopes
 * @param startLift the lift that the skier should take at the start of the route,
 *                  or null if the route starts with a slope
 * @param plannedStartTime the planned start time for the route
 * @param plannedEndTime the planned end time for the route
 * @author uqibg
 */
public record RoutePlan(List<Node> steps, Lift startLift, LocalTime plannedStartTime, LocalTime plannedEndTime) {
    private static final int ZERO_SCORE = 0;

    /**
     * Constructor for RoutePlan.
     * @param steps the list of steps in the planned route, which can be either lifts or slopes
     * @param startLift the lift that the skier should take at the start of the route,
     *                  or null if the route starts with a slope
     * @param plannedStartTime the planned start time for the route
     * @param plannedEndTime the planned end time for the route
     */
    public RoutePlan(List<Node> steps, Lift startLift,
                     LocalTime plannedStartTime, LocalTime plannedEndTime) {
        this.steps = steps == null ? List.of() : List.copyOf(steps);
        this.startLift = startLift;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
    }

    /**
     * A getter for the step at the specified index in the route plan.
     * @param index the index of the step to retrieve
     * @return the step at the specified index in the route plan
     */
    public Node getStep(int index) {
        return this.steps.get(index);
    }

    /**
     * A getter for the number of steps in the route plan.
     * @return the number of steps in the route plan
     */
    public int size() {
        return this.steps.size();
    }

    /**
     * A getter for the list of slopes in the route plan.
     * @return a list of the slopes in the route plan, in the order they appear in the steps list
     */
    public List<Slope> getSlopes() {
        List<Slope> slopes = new ArrayList<>();
        for (Node step : this.steps) {
            if (step instanceof Slope slope) {
                slopes.add(slope);
            }
        }
        return List.copyOf(slopes);
    }

    /**
     * Calculates the total distance of the route plan by summing up the lengths of all slopes in the route.
     * @return the total distance of the route plan in meters
     */
    public int getTotalDistance() {
        int totalDistance = ZERO_SCORE;
        for (Slope slope : getSlopes()) {
            totalDistance += slope.getLengthMeters();
        }
        return totalDistance;
    }

    /**
     * Calculates the total elevation of the route plan by summing up the elevation meters of all slopes in the route.
     * @return the total elevation of the route plan in meters
     */
    public int getTotalElevation() {
        int totalElevation = ZERO_SCORE;
        for (Slope slope : getSlopes()) {
            totalElevation += slope.getElevationMeters();
        }
        return totalElevation;
    }

    /**
     * A getter for the number of slopes in the route plan.
     * @return the number of slopes in the route plan
     */
    public int getSlopeCount() {
        return getSlopes().size();
    }

    /**
     * Calculates the number of unique slopes in the route plan by counting the distinct slope IDs.
     * @return the number of unique slopes in the route plan
     */
    public int getUniqueSlopeCount() {
        Set<String> uniqueSlopeIds = new HashSet<>();
        for (Slope slope : getSlopes()) {
            uniqueSlopeIds.add(slope.getId());
        }
        return uniqueSlopeIds.size();
    }

    /**
     * Calculates the preference score of the route plan
     * based on the given preference profile by summing up the scores of all slopes in the route.
     * @param preferences the preference profile to use for calculating the preference score of the route plan
     * @return the preference score of the whole route plan
     */
    public int getPreferenceScore(PreferenceProfile preferences) {
        if (preferences == null) {
            return ZERO_SCORE;
        }

        int score = ZERO_SCORE;
        for (Slope slope : getSlopes()) {
            score += preferences.score(slope);
        }
        return score;
    }
}

