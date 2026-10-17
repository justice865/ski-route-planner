package edu.kit.kastel.model.graph.types;

/**
 * The TravelTimeResult record represents the result of calculating the travel time for a slope.
 * @param status the status of the slope travel time calculation
 * @param seconds the travel time in seconds
 * @author uqibg
 */
public record TravelTimeResult(SlopeTravelStatus status, long seconds) {
}


