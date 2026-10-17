package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The TakeCommand class represents the command to take the next step in the ski route.
 * @author uqibg
 */
public class TakeCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final String ERROR_ARGS = "Error, take takes no arguments";
    private static final String ERROR_NO_ACTIVE_ROUTE = "Error, no active route";
    private static final String ERROR_NEXT_NOT_MARKED = "Error, the next step is not marked yet";

    private final SkiSession session;

    /**
     * Constructor for the TakeCommand.
     * @param session the SkiSession to operate on
     */
    public TakeCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }
        if (!this.session.hasActiveRoute() || !this.session.hasRemainingStep()) {
            System.err.println(ERROR_NO_ACTIVE_ROUTE);
            return;
        }
        if (!this.session.isNextStepMarked()) {
            System.err.println(ERROR_NEXT_NOT_MARKED);
            return;
        }
        this.session.take();
    }
}

