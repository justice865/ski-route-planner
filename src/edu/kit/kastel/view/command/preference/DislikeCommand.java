package edu.kit.kastel.view.command.preference;

import edu.kit.kastel.model.graph.types.Difficulty;
import edu.kit.kastel.model.graph.types.Surface;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The command class that handles the dislike command.
 * @author uqibg
 */
public class DislikeCommand extends Command {
    private static final int ONE_ARG = 1;
    private static final int FIRST_ARG_INDEX = 0;

    private static final String ERROR_ARGS = "Error, dislike command requires exactly one argument";
    private static final String ERROR_UNKNOWN_VALUE = "Error, unknown preference value";

    private final SkiSession session;

    /**
     * Constructor for the DislikeCommand.
     * @param session the SkiSession to operate on
     */
    public DislikeCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != ONE_ARG) {
            System.err.println(ERROR_ARGS);
            return;
        }

        String value = args[FIRST_ARG_INDEX].toUpperCase();
        Difficulty difficulty = parseDifficulty(value);
        if (difficulty != null) {
            this.session.dislikeDifficulty(difficulty);
            return;
        }

        Surface surface = parseSurface(value);
        if (surface != null) {
            this.session.dislikeSurface(surface);
            return;
        }

        System.err.println(ERROR_UNKNOWN_VALUE);
    }

    private Difficulty parseDifficulty(String value) {
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.name().equals(value)) {
                return difficulty;
            }
        }
        return null;
    }

    private Surface parseSurface(String value) {
        for (Surface surface : Surface.values()) {
            if (surface.name().equals(value)) {
                return surface;
            }
        }
        return null;
    }
}

