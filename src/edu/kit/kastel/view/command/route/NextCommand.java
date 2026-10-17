package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The NextCommand class represents a command that retrieves the next step in the ski route.
 * @author uqibg
 */
public class NextCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final String ERROR_ARGS = "Error, next takes no arguments";
    private static final String ERROR_NO_NEXT = "Error, no next step available";
    private static final String MESSAGE_ROUTE_FINISHED = "route finished!";

    private final SkiSession session;

    /**
     * Constructor for the NextCommand.
     * @param session the SkiSession to operate on
     */
    public NextCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }

        Node next = this.session.next();
        if (next == null) {
            if (this.session.hasActiveRoute() && this.session.showRoute().isEmpty()) {
                System.out.println(MESSAGE_ROUTE_FINISHED);
                this.session.abort();
            } else {
                System.err.println(ERROR_NO_NEXT);
            }
            return;
        }
        System.out.println(next.getId());
    }
}

