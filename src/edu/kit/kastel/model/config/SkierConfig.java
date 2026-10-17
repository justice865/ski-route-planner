package edu.kit.kastel.model.config;

/**
 * The SkierConfig class represents the configuration of a skier, including their skill level, goal, and preferences.
 * @author uqibg
 */
public class SkierConfig {
    private Skill skill;
    private Goal goal;
    private final PreferenceProfile preferences;

    /**
     * Constructor for the SkierConfig class. Initializes the preferences to a new PreferenceProfile.
     */
    public SkierConfig() {
        this.preferences = new PreferenceProfile();
    }

    /**
     * Gets the skill level of the skier.
     * @return the skill level of the skier
     */
    public Skill getSkill() {
        return this.skill;
    }

    /**
     * Sets the skill level of the skier.
     * @param skill the skill level to set for the skier
     */
    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    /**
     * Gets the goal of the skier.
     * @return the goal of the skier
     */
    public Goal getGoal() {
        return this.goal;
    }

    /**
     * Sets the goal of the skier.
     * @param goal the goal to set for the skier
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    /**
     * Gets the preference profile of the skier.
     * @return the preference profile of the skier
     */
    public PreferenceProfile getPreferences() {
        return this.preferences;
    }

    /**
     * Checks if the skier has a skill level set.
     * @return true if the skier has a skill level set, false otherwise
     */
    public boolean hasSkill() {
        return this.skill != null;
    }

    /**
     * Checks if the skier has a goal set.
     * @return true if the skier has a goal set, false otherwise
     */
    public boolean hasGoal() {
        return this.goal != null;
    }

    /**
     * Checks if the skier is ready for planning.
     * @return true if the skier has both a skill level and a goal set, false otherwise
     */
    public boolean isReadyForPlanning() {
        return hasSkill() && hasGoal();
    }
}

