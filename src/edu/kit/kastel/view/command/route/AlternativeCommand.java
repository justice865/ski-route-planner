package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import java.util.List;

/**
 * The Command that is specified to find an alternative route to the current one.
 * @author uqibg
 */
public class AlternativeCommand extends Command {
    private static final int NO_ARGS = 0;
    private static final int FIRST_STEP_INDEX = 0;

    private static final String AVOIDED_PREFIX = "avoided ";
    private static final String ERROR_ARGS = "Error, alternative takes no arguments";
    private static final String ERROR_NO_ACTIVE_ROUTE = "Error, no active route";
    private static final String ERROR_ROUTE_NOT_STARTED = "Error, route has not started yet";
    private static final String ERROR_NEXT_NOT_MARKED = "Error, the next step is not marked yet";
    private static final String MESSAGE_NO_ALTERNATIVE = "no alternative found";

    private final SkiSession session;

    /**
     * Constructor for the AlternativeCommand.
     * @param session the SkiSession to operate on
     */
    public AlternativeCommand(SkiSession session) {
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
        if (!this.session.hasRouteStarted()) {
            System.err.println(ERROR_ROUTE_NOT_STARTED);
            return;
        }
        if (!this.session.isNextStepMarked()) {
            System.err.println(ERROR_NEXT_NOT_MARKED);
            return;
        }

        List<Node> remaining = this.session.showRoute();
        if (remaining.isEmpty()) {
            System.out.println(MESSAGE_NO_ALTERNATIVE);
            return;
        }

        String forbiddenId = remaining.get(FIRST_STEP_INDEX).getId();
        if (this.session.alternative()) {
            System.out.println(AVOIDED_PREFIX + forbiddenId);
        } else {
            System.out.println(MESSAGE_NO_ALTERNATIVE);
        }
    }
}
