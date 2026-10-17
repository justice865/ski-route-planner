package edu.kit.kastel.model.planning;

import edu.kit.kastel.model.config.SkierConfig;
import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.elements.Slope;

import java.time.LocalTime;
import java.util.List;
/**
 * The RouteProgress class represents the progress of a skier along a route plan.
 * @author uqibg
 */
public class RouteProgress {
    private static final int FIRST_STEP_INDEX = 0;

    private RoutePlan routePlan;
    private int nextIndex;
    private Node currentNode;
    private LocalTime currentTime;
    private boolean started;
    private boolean awaitingTake;

    /**
     * Constructor for RouteProgress.
     * @param routePlan the route plan to follow
     * @param startTime the starting time of the route
     */
    public RouteProgress(RoutePlan routePlan, LocalTime startTime) {
        this.routePlan = routePlan;
        this.nextIndex = FIRST_STEP_INDEX;
        this.currentNode = null;
        this.currentTime = startTime;
        this.started = false;
        this.awaitingTake = false;
    }

    /**
     * A getter for the current node.
     * @return the current node
     */
    public Node getCurrentNode() {
        return this.currentNode;
    }

    /**
     * A getter for the current time.
     * @return the current time
     */
    public LocalTime getCurrentTime() {
        return this.currentTime;
    }

    /**
     * A getter for whether the route has been started.
     * @return true if the route has been started, false otherwise
     */
    public boolean hasStarted() {
        return this.started;
    }

    /**
     * A getter for whether the next step has been requested but not yet taken.
     * @return true if the next step has been requested but not yet taken, false otherwise
     */
    public boolean isAwaitingTake() {
        return this.awaitingTake;
    }

    /**
     * Checks if there is a next step in the route plan.
     * @return true if there is a next step in the route plan, false otherwise
     */
    public boolean hasNextStep() {
        return this.routePlan != null && this.nextIndex < this.routePlan.size();
    }

    /**
     * Peeks at the next step in the route plan without advancing the progress.
     * @return the next node in the route plan, or null if there is no next step
     */
    public Node peekNextStep() {
        if (this.hasNextStep()) {
            return this.routePlan.getStep(this.nextIndex);
        } else  {
            return null;
        }
    }

    /**
     * Gets the remaining steps in the route plan from the current progress.
     * @return a list of the remaining nodes in the route plan, or an empty list if there is no next step
     */
    public List<Node> getRemainingSteps() {
        if (this.routePlan == null || !hasNextStep()) {
            return List.of();
        }
        return List.copyOf(this.routePlan.steps().subList(this.nextIndex, this.routePlan.size()));
    }

    /**
     * Marks the next step, checking if the config is valid before marking.
     * @param config the SkierConfig to check before marking the next step
     */
    public void markNextRequested(SkierConfig config) {
        if (config == null || !config.hasSkill() || !config.hasGoal()) {
            return;
        }
        if (this.hasNextStep() && !this.awaitingTake) {
            this.awaitingTake = true;
        }
    }

    /**
     * Checks if the next step can be taken.
     * @return true if the next step can be taken, false otherwise
     */
    public boolean canTake() {
        return this.awaitingTake && this.hasNextStep();
    }

    /**
     * Clears a pending next-step mark without changing position or time.
     */
    public void clearNextRequest() {
        this.awaitingTake = false;
    }

    /**
     * Takes the next step in the route plan, updating the current node and time accordingly.
     * @param config the SkierConfig to check before taking the next step
     */
    public void takeNextStep(SkierConfig config) {
        if (!canTake() || this.currentTime == null || config == null || !config.hasSkill() || !config.hasGoal()) {
            return;
        }

        Node nextNode = peekNextStep();
        if (nextNode == null) {
            return;
        }

        LocalTime updatedTime = this.currentTime;
        if (nextNode instanceof Lift lift) {
            updatedTime = lift.getArrivalTime(this.currentTime);
        } else if (nextNode instanceof Slope slope) {
            updatedTime = this.currentTime.plusSeconds(slope.getTravelSeconds(config.getSkill()));
        }

        this.currentTime = updatedTime;
        this.currentNode = nextNode;
        this.nextIndex++;
        this.started = true;
        this.awaitingTake = false;
    }

    /**
     * Resets the progress to the beginning of the given route plan, starting at the given time and node.
     * @param routePlan the new route plan to follow
     * @param startTime the new starting time of the route
     * @param currentNode the new current node of the route
     */
    public void reset(RoutePlan routePlan, LocalTime startTime, Node currentNode) {
        this.routePlan = routePlan;
        this.nextIndex = FIRST_STEP_INDEX;
        this.currentNode = currentNode;
        this.currentTime = startTime;
        this.started = currentNode != null;
        this.awaitingTake = false;
    }
}

