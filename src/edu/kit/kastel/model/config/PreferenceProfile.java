package edu.kit.kastel.model.config;

import edu.kit.kastel.model.graph.elements.Slope;
import edu.kit.kastel.model.graph.types.Difficulty;
import edu.kit.kastel.model.graph.types.Surface;

import java.util.HashSet;
import java.util.Set;

/**
 * The PreferenceProfile class represents a skier's preferences for certain difficulties and surfaces
 * of a slope.
 * @author uqibg
 */
public class PreferenceProfile {
    private static final int NEUTRAL_SCORE = 0;
    private static final int LIKED_DELTA = 1;
    private static final int DISLIKED_DELTA = -1;

    private final Set<Difficulty> likedDifficulties;
    private final Set<Difficulty> dislikedDifficulties;
    private final Set<Surface> likedSurfaces;
    private final Set<Surface> dislikedSurfaces;

    /**
     * A constructor for PreferenceProfile.
     */
    public PreferenceProfile() {
        this.likedDifficulties = new HashSet<>();
        this.dislikedDifficulties = new HashSet<>();
        this.likedSurfaces = new HashSet<>();
        this.dislikedSurfaces = new HashSet<>();
    }

    /**
     * Marks a difficulty as liked by the skier. If the difficulty was previously disliked,
     * it will be removed from the disliked difficulties set.
     * @param difficulty the difficulty to be marked as liked
     */
    public void like(Difficulty difficulty) {
        this.dislikedDifficulties.remove(difficulty);
        this.likedDifficulties.add(difficulty);
    }

    /**
     * Marks a difficulty as disliked by the skier. If the difficulty was previously liked,
     * it will be removed from the liked difficulties set.
     * @param difficulty the difficulty to be marked as disliked
     */
    public void dislike(Difficulty difficulty) {
        this.likedDifficulties.remove(difficulty);
        this.dislikedDifficulties.add(difficulty);
    }

    /**
     * Marks a surface as liked by the skier. If the surface was previously disliked,
     * it will be removed from the disliked surfaces set.
     * @param surface the surface to be marked as liked
     */
    public void like(Surface surface) {
        this.dislikedSurfaces.remove(surface);
        this.likedSurfaces.add(surface);
    }

    /**
     * Marks a surface as disliked by the skier. If the surface was previously liked,
     * it will be removed from the liked surfaces set.
     * @param surface the surface to be marked as disliked
     */
    public void dislike(Surface surface) {
        this.likedSurfaces.remove(surface);
        this.dislikedSurfaces.add(surface);
    }

    /**
     * Resets all preferences, clearing both liked and disliked difficulties and surfaces.
     */
    public void reset() {
        this.likedDifficulties.clear();
        this.dislikedDifficulties.clear();
        this.likedSurfaces.clear();
        this.dislikedSurfaces.clear();
    }

    /**
     * Calculates a score for a given slope based on the skier's preferences.
     * The score is increased by 1 for each liked and decreased by 1 for each disliked attribute of the slope.
     * @param slope the slope for which the score is to be calculated
     * @return the calculated score for the slope based on the skier's preferences
     */
    public int score(Slope slope) {
        int score = NEUTRAL_SCORE;

        Difficulty difficulty = slope.getDifficulty();
        if (this.likedDifficulties.contains(difficulty)) {
            score += LIKED_DELTA;
        } else if (this.dislikedDifficulties.contains(difficulty)) {
            score += DISLIKED_DELTA;
        }

        Surface surface = slope.getSurface();
        if (this.likedSurfaces.contains(surface)) {
            score += LIKED_DELTA;
        } else if (this.dislikedSurfaces.contains(surface)) {
            score += DISLIKED_DELTA;
        }

        return score;
    }
}

