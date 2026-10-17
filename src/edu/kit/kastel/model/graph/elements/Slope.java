package edu.kit.kastel.model.graph.elements;

import edu.kit.kastel.model.config.Skill;
import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.types.Difficulty;
import edu.kit.kastel.model.graph.types.SlopeTravelStatus;
import edu.kit.kastel.model.graph.types.Surface;
import edu.kit.kastel.model.graph.types.TravelTimeResult;

/**
 * The Slope class represents a ski slope in the ski area.
 * @author uqibg
 */
public class Slope extends Node {
    private static final long ZERO_SECONDS = 0L;
    private static final int NON_POSITIVE_LENGTH_THRESHOLD = 0;

    private static final double LENGTH_DIVISOR = 8.0;
    private static final double BASE_FACTOR = 1.0;
    private static final double ELEVATION_RATIO_FACTOR = 2.0;

    private static final double DIFFICULTY_BLUE_MULTIPLIER = 1.00;
    private static final double DIFFICULTY_RED_MULTIPLIER = 1.15;
    private static final double DIFFICULTY_BLACK_MULTIPLIER = 1.35;

    private static final double SURFACE_REGULAR_MULTIPLIER = 1.00;
    private static final double SURFACE_BUMPY_MULTIPLIER = 1.20;
    private static final double SURFACE_ICY_MULTIPLIER = 1.30;

    private static final double SKILL_BEGINNER_MULTIPLIER = 1.35;
    private static final double SKILL_INTERMEDIATE_MULTIPLIER = 1.10;
    private static final double SKILL_EXPERT_MULTIPLIER = 0.90;

    private final Difficulty difficulty;
    private final Surface surface;
    private final int lengthMeters;
    private final int elevationMeters;

    /**
     * Constructor for Slope.
     * @param id the id of the slope
     * @param difficulty the difficulty level of the slope
     * @param surface the surface type of the slope
     * @param lengthMeters the length of the slope in meters
     * @param elevationMeters the elevation change of the slope in meters
     */
    public Slope(String id, Difficulty difficulty, Surface surface,
                 int lengthMeters, int elevationMeters) {
        super(id);
        this.difficulty = difficulty;
        this.surface = surface;
        this.lengthMeters = lengthMeters;
        this.elevationMeters = elevationMeters;
    }

    /**
     * A getter for the difficulty level of the slope.
     * @return the difficulty level of the slope
     */
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * A getter for the surface type of the slope.
     * @return the surface type of the slope
     */
    public Surface getSurface() {
        return this.surface;
    }

    /**
     * A getter for the length of the slope in meters.
     * @return the length of the slope in meters
     */
    public int getLengthMeters() {
        return this.lengthMeters;
    }

    /**
     * A getter for the elevation change of the slope in meters.
     * @return the elevation change of the slope in meters
     */
    public int getElevationMeters() {
        return this.elevationMeters;
    }

    /**
     * Calculates the estimated travel time in seconds for a given skill level on this slope.
     * @param skill the skill level of the skier
     * @return the estimated travel time in seconds
     */
    public long getTravelSeconds(Skill skill) {
        return this.calculateTravelSeconds(skill).seconds();
    }

    /**
     * Calculates the travel time and creates a TravelTimeResult containing the status and the estimated travel time in seconds.
     * @param skill the skill level of the skier
     * @return a TravelTimeResult containing the status and the estimated travel time in seconds
     */
    public TravelTimeResult calculateTravelSeconds(Skill skill) {
        if (skill == null) {
            return new TravelTimeResult(SlopeTravelStatus.SKILL_MISSING, ZERO_SECONDS);
        }
        if (this.lengthMeters <= NON_POSITIVE_LENGTH_THRESHOLD) {
            return new TravelTimeResult(SlopeTravelStatus.LENGTH_NOT_POSITIVE, ZERO_SECONDS);
        }
        if (this.difficulty == null) {
            return new TravelTimeResult(SlopeTravelStatus.DIFFICULTY_MISSING, ZERO_SECONDS);
        }
        if (this.surface == null) {
            return new TravelTimeResult(SlopeTravelStatus.SURFACE_MISSING, ZERO_SECONDS);
        }

        double length = this.lengthMeters;
        double r = Math.abs((double) this.elevationMeters) / length;
        double time = (length / LENGTH_DIVISOR)
                * difficultyMultiplier(this.difficulty)
                * surfaceMultiplier(this.surface)
                * (BASE_FACTOR + (ELEVATION_RATIO_FACTOR * r))
                * skillMultiplier(skill);
        return new TravelTimeResult(SlopeTravelStatus.SUCCESS, (long) Math.ceil(time));
    }

    private static double difficultyMultiplier(Difficulty difficulty) {
        return switch (difficulty) {
            case BLUE -> DIFFICULTY_BLUE_MULTIPLIER;
            case RED -> DIFFICULTY_RED_MULTIPLIER;
            case BLACK -> DIFFICULTY_BLACK_MULTIPLIER;
        };
    }

    private static double surfaceMultiplier(Surface surface) {
        return switch (surface) {
            case REGULAR -> SURFACE_REGULAR_MULTIPLIER;
            case BUMPY -> SURFACE_BUMPY_MULTIPLIER;
            case ICY -> SURFACE_ICY_MULTIPLIER;
        };
    }

    private static double skillMultiplier(Skill skill) {
        return switch (skill) {
            case BEGINNER -> SKILL_BEGINNER_MULTIPLIER;
            case INTERMEDIATE -> SKILL_INTERMEDIATE_MULTIPLIER;
            case EXPERT -> SKILL_EXPERT_MULTIPLIER;
        };
    }
}

