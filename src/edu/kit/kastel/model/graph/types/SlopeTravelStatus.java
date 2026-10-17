package edu.kit.kastel.model.graph.types;

/**
 * The SlopeTravelStatus enum represents the different statuses that can occur when trying to travel a slope.
 * @author uqibg
 */
public enum SlopeTravelStatus {
    /**
     * represents the status of successfully traveling a slope.
     */
    SUCCESS,
    /**
     * represents the status of not having the skill attribute present.
     */
    SKILL_MISSING,
    /**
     * represents the status of not having the length attribute present or the length being not positive.
     */
    LENGTH_NOT_POSITIVE,
    /**
     * represents the status of not having the difficulty attribute present.
     */
    DIFFICULTY_MISSING,
    /**
     * represents the status of not having the surface attribute present.
     */
    SURFACE_MISSING
}


