package edu.kit.kastel.model.graph.elements;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.types.LiftType;

import java.time.LocalTime;

/**
 * The class that represents a lift in ski area.
 * @author uqibg
 */
public class Lift extends Node {
    private final LiftType type;
    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int rideDurationMinutes;
    private final int waitingTimeMinutes;
    private final boolean transit;

    /**
     * Constructor for Lift.
     * @param id the id of the lift
     * @param type the type of the lift
     * @param openingTime the opening time of the lift
     * @param closingTime the closing time of the lift
     * @param rideDurationMinutes the duration of the ride in minutes
     * @param waitingTimeMinutes the waiting time in minutes
     * @param transit whether the lift is a transit lift
     */
    public Lift(String id, LiftType type, LocalTime openingTime, LocalTime closingTime,
                int rideDurationMinutes, int waitingTimeMinutes, boolean transit) {
        super(id);
        this.type = type;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.rideDurationMinutes = rideDurationMinutes;
        this.waitingTimeMinutes = waitingTimeMinutes;
        this.transit = transit;
    }

    /**
     * A getter for the type of the lift.
     * @return the type of the lift
     */
    public LiftType getType() {
        return this.type;
    }

    /**
     * A getter for the opening time of the lift.
     * @return the opening time of the lift
     */
    public LocalTime getOpeningTime() {
        return this.openingTime;
    }

    /**
     * A getter for the closing time of the lift.
     * @return the closing time of the lift
     */
    public LocalTime getClosingTime() {
        return this.closingTime;
    }

    /**
     * A getter for the duration of the ride in minutes.
     * @return the duration of the ride in minutes
     */
    public int getRideDurationMinutes() {
        return this.rideDurationMinutes;
    }

    /**
     * A getter for the waiting time in minutes.
     * @return the waiting time in minutes
     */
    public int getWaitingTimeMinutes() {
        return this.waitingTimeMinutes;
    }

    /**
     * A getter for whether the lift is a transit lift.
     * @return whether the lift is a transit lift
     */
    public boolean isTransit() {
        return this.transit;
    }

    /**
     * Checks if the lift can be taken at the given time.
     * @param currentTime the current time
     * @return true if the lift can be taken at the given time, false otherwise
     */
    public boolean canBeTakenAt(LocalTime currentTime) {
        if (currentTime == null || this.openingTime == null || this.closingTime == null) {
            return false;
        }
        LocalTime departureTime = getDepartureTime(currentTime);
        return !departureTime.isAfter(this.closingTime);
    }

    /**
     * Calculates the departure time if the lift is taken at the given time.
     * @param currentTime the current time
     * @return the departure time if the lift is taken at the given time
     */
    public LocalTime getDepartureTime(LocalTime currentTime) {
        LocalTime earliestDeparture = currentTime.plusMinutes(this.waitingTimeMinutes);
        if (earliestDeparture.isBefore(this.openingTime)) {
            return this.openingTime;
        }
        return earliestDeparture;
    }

    /**
     * Calculates the arrival time if the lift is taken at the given time.
     * @param currentTime the current time
     * @return the arrival time if the lift is taken at the given time
     */
    public LocalTime getArrivalTime(LocalTime currentTime) {
        LocalTime departureTime = getDepartureTime(currentTime);
        return departureTime.plusMinutes(this.rideDurationMinutes);
    }
}

