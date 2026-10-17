package edu.kit.kastel.model.planning;

import edu.kit.kastel.model.config.Goal;
import edu.kit.kastel.model.config.Skill;
import edu.kit.kastel.model.config.SkierConfig;
import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.core.SkiArea;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.types.Difficulty;
import edu.kit.kastel.model.graph.types.Surface;

import java.time.LocalTime;
import java.util.List;

/**
 * The SkiSession class represents a skiing session for a user.
 * @author uqibg
 */
public class SkiSession {
    private SkiArea skiArea;
    private final SkierConfig skierConfig;
    private RoutePlan activePlan;
    private RouteProgress progress;
    private final RoutePlanner planner;

    /**
     * A constructor for SkiSession.
     */
    public SkiSession() {
        this.skierConfig = new SkierConfig();
        this.planner = new RoutePlanner();
        this.skiArea = null;
        this.activePlan = null;
        this.progress = null;
    }

    /**
     * A getter for the currently loaded ski area.
     * @return the currently loaded ski area, or null if no area is loaded
     */
    public SkiArea getSkiArea() {
        return this.skiArea;
    }

    /**
     * A getter for the skier configuration.
     * @return the skier configuration
     */
    public SkierConfig getSkierConfig() {
        return this.skierConfig;
    }

    /**
     * Checks if a ski area has been loaded into the session.
     * @return true if a ski area is loaded, false otherwise
     */
    public boolean hasLoadedArea() {
        return skiArea != null;
    }

    /**
     * Checks if there is an active route plan in the session.
     * @return true if there is an active route plan, false otherwise
     */
    public boolean hasActiveRoute() {
        return this.activePlan != null && this.progress != null;
    }

    /**
     * Checks if the current active route still has a next step.
     * @return true if an active route exists and has another step
     */
    public boolean hasRemainingStep() {
        return hasActiveRoute() && this.progress.hasNextStep();
    }

    /**
     * Checks if a next step is currently marked and waiting for take.
     * @return true if next has been called and take is expected
     */
    public boolean isNextStepMarked() {
        return hasActiveRoute() && this.progress.isAwaitingTake();
    }

    /**
     * Checks if the active route has already started by taking at least one step.
     * @return true if an active route exists and progress has started
     */
    public boolean hasRouteStarted() {
        return hasActiveRoute() && this.progress.hasStarted();
    }

    /**
     * Clears the currently marked next step, if any.
     */
    public void clearNextStepMark() {
        if (!hasActiveRoute()) {
            return;
        }
        this.progress.clearNextRequest();
    }

    /**
     * Loads a ski area into the session, replacing any previously loaded area and aborting any active route.
     * @param skiArea the ski area to load into the session
     */
    public void loadArea(SkiArea skiArea) {
        this.skiArea = skiArea;
        abort();
    }

    /**
     * Sets the skill level of the skier in the session, and replans the route if there is an active route.
     * @param skill the skill level to set for the skier
     */
    public void setSkill(Skill skill) {
        this.skierConfig.setSkill(skill);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Sets the goal of the skier in the session, and replans the route if there is an active route.
     * @param goal the goal to set for the skier
     */
    public void setGoal(Goal goal) {
        this.skierConfig.setGoal(goal);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Likes a difficulty level for the skier in the session, and replans the route if there is an active route.
     * @param difficulty the difficulty level to like for the skier
     */
    public void likeDifficulty(Difficulty difficulty) {
        this.skierConfig.getPreferences().like(difficulty);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Dislikes a difficulty level for the skier in the session, and replans the route if there is an active route.
     * @param difficulty the difficulty level to dislike for the skier
     */
    public void dislikeDifficulty(Difficulty difficulty) {
        this.skierConfig.getPreferences().dislike(difficulty);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Likes a surface type for the skier in the session, and replans the route if there is an active route.
     * @param surface the surface type to like for the skier
     */
    public void likeSurface(Surface surface) {
        this.skierConfig.getPreferences().like(surface);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Dislikes a surface type for the skier in the session, and replans the route if there is an active route.
     * @param surface the surface type to dislike for the skier
     */
    public void dislikeSurface(Surface surface) {
        this.skierConfig.getPreferences().dislike(surface);
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Resets all preferences for the skier in the session, and replans the route if there is an active route.
     */
    public void resetPreferences() {
        this.skierConfig.getPreferences().reset();
        if (hasActiveRoute()) {
            replanFromCurrentState();
        }
    }

    /**
     * Plans a route for the skier in the session.
     * @param startLiftId the ID of the lift to start the route from
     * @param startTime the time to start the route
     * @param endTime the time to end the route
     */
    public void plan(String startLiftId, LocalTime startTime, LocalTime endTime) {
        if (!hasLoadedArea() || !this.skierConfig.isReadyForPlanning()) {
            return;
        }
        if (startTime == null || endTime == null || !startTime.isBefore(endTime)) {
            return;
        }

        Lift startLift = requireStartLift(startLiftId);
        if (startLift == null || !startLift.isTransit()) {
            return;
        }

        RoutePlan planned = this.planner.plan(this.skiArea, this.skierConfig, startLift, startTime, endTime);
        if (planned == null) {
            abort();
            return;
        }

        this.activePlan = planned;
        this.progress = new RouteProgress(planned, startTime);
    }

    /**
     * Aborts the current active route, if any, by clearing the active plan and progress.
     */
    public void abort() {
        this.activePlan = null;
        this.progress = null;
    }

    /**
     * Returns the next node in the active route without advancing the progress.
     * @return the next node in the active route, or null if there is no active route or no next node
     */
    public Node next() {
        if (!hasActiveRoute()) {
            return null;
        }
        this.progress.markNextRequested(this.skierConfig);
        return this.progress.peekNextStep();
    }

    /**
     * Advances the progress to the next step in the active route.
     */
    public void take() {
        if (!hasActiveRoute()) {
            return;
        }
        this.progress.takeNextStep(this.skierConfig);
    }

    /**
     * Attempts to find an alternative route from the current state in the active route.
     * @return true if an alternative route was found and set as the active route, false otherwise
     */
    public boolean alternative() {
        if (!hasActiveRoute()) {
            return false;
        }
        if (!this.progress.hasStarted() || this.progress.getCurrentNode() == null || this.progress.getCurrentTime() == null) {
            return false;
        }

        Node currentNode = this.progress.getCurrentNode();
        LocalTime currentTime = this.progress.getCurrentTime();
        LocalTime endTime = this.activePlan.plannedEndTime();
        Node forbiddenNext = this.progress.peekNextStep();

        RoutePlan alt = this.planner.planAlternative(
                this.skiArea,
                this.skierConfig,
                currentNode,
                currentTime,
                endTime,
                forbiddenNext
        );
        if (alt == null) {
            return false;
        }

        this.activePlan = alt;
        this.progress.reset(alt, currentTime, currentNode);
        return true;
    }

    /**
     * Returns the remaining steps in the active route, starting from the current progress.
     * @return the remaining steps in the active route, or an empty list if there is no active route
     */
    public List<Node> showRoute() {
        if (!hasActiveRoute()) {
            return List.of();
        }
        if (this.progress.hasStarted()) {
            return this.progress.getRemainingSteps();
        }
        return this.activePlan.steps();
    }

    private void replanFromCurrentState() {
        if (!hasActiveRoute() || !hasLoadedArea() || !this.skierConfig.isReadyForPlanning()) {
            abort();
            return;
        }

        RoutePlan replanned;
        if (this.progress.hasStarted() && this.progress.getCurrentNode() != null && this.progress.getCurrentTime() != null) {
            Node currentNode = this.progress.getCurrentNode();
            LocalTime currentTime = this.progress.getCurrentTime();
            replanned = this.planner.planAlternative(
                    this.skiArea,
                    this.skierConfig,
                    currentNode,
                    currentTime,
                    this.activePlan.plannedEndTime(),
                    null
            );
        } else {
            Lift startLift = this.activePlan.startLift();
            LocalTime startTime = this.activePlan.plannedStartTime();
            LocalTime endTime = this.activePlan.plannedEndTime();
            if (startLift == null || startTime == null || endTime == null) {
                abort();
                return;
            }
            replanned = this.planner.plan(this.skiArea, this.skierConfig, startLift, startTime, endTime);
        }

        if (replanned == null) {
            abort();
            return;
        }

        this.activePlan = replanned;
        if (this.progress.hasStarted()) {
            this.progress.reset(replanned, this.progress.getCurrentTime(), this.progress.getCurrentNode());
        } else {
            this.progress.reset(replanned, this.activePlan.plannedStartTime(), null);
        }
    }

    private Lift requireStartLift(String startLiftId) {
        if (startLiftId == null || this.skiArea == null) {
            return null;
        }
        Node node = this.skiArea.getNode(startLiftId);
        if (node instanceof Lift lift) {
            return lift;
        }
        return null;
    }
}

