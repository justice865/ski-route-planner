package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import java.util.List;

/**
 * The Command that is specified to show the current route.
 * @author uqibg
 */
public class ShowRouteCommand extends Command {
    private static final int NO_ARGS = 0;
    private static final int ZERO = 0;

    private static final char STEP_SEPARATOR = ' ';

    private static final String ERROR_ARGS = "Error, show route takes no arguments";
    private static final String ERROR_NO_ACTIVE_ROUTE = "Error, no active route";

    private final SkiSession session;

    /**
     * Constructor for the ShowRouteCommand.
     * @param session the SkiSession to operate on
     */
    public ShowRouteCommand(SkiSession session) {
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

        this.session.clearNextStepMark();
        List<Node> route = this.session.showRoute();
        if (route.isEmpty()) {
            System.err.println(ERROR_NO_ACTIVE_ROUTE);
            return;
        }

        StringBuilder text = new StringBuilder();
        for (int i = ZERO; i < route.size(); i++) {
            if (i > ZERO) {
                text.append(STEP_SEPARATOR);
            }
            text.append(route.get(i).getId());
        }
        System.out.println(text);
    }
}

