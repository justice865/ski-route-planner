package edu.kit.kastel.view.command.preference;

import edu.kit.kastel.model.config.Goal;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The SetGoalCommand class represents a command to set the goal of the current ski session.
 * @author uqibg
 */
public class SetGoalCommand extends Command {
    private static final int ONE_ARG = 1;
    private static final int FIRST_ARG_INDEX = 0;

    private static final String ERROR_ARGS = "Error, set goal requires exactly one argument";
    private static final String ERROR_INVALID_GOAL = "Error, invalid goal";

    private final SkiSession session;

    /**
     * Constructor for the SetGoalCommand.
     * @param session the SkiSession to operate on
     */
    public SetGoalCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != ONE_ARG) {
            System.err.println(ERROR_ARGS);
            return;
        }

        Goal goal = parseGoal(args[FIRST_ARG_INDEX]);
        if (goal == null) {
            System.err.println(ERROR_INVALID_GOAL);
            return;
        }
        this.session.setGoal(goal);
    }

    private Goal parseGoal(String value) {
        if (value == null) {
            return null;
        }
        String upper = value.toUpperCase();
        for (Goal goal : Goal.values()) {
            if (goal.name().equals(upper)) {
                return goal;
            }
        }
        return null;
    }
}

