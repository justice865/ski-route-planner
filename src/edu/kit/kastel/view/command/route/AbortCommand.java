package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The Command that is specified to abort the current route.
 * @author uqibg
 */
public class AbortCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final String ERROR_ARGS = "Error, abort takes no arguments";
    private static final String ERROR_NO_ACTIVE_ROUTE = "Error, no active route";
    private static final String MESSAGE_ABORTED = "route aborted";

    private final SkiSession session;

    /**
     * Constructor for the AbortCommand.
     * @param session the SkiSession to operate on
     */
    public AbortCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }
        if (!this.session.hasActiveRoute()) {
            System.err.println(ERROR_NO_ACTIVE_ROUTE);
            return;
        }

        this.session.abort();
        System.out.println(MESSAGE_ABORTED);
    }
}
