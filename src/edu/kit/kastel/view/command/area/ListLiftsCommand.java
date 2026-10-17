package edu.kit.kastel.view.command.area;

import edu.kit.kastel.model.graph.core.SkiArea;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import java.util.List;

/**
 * Command to list all lifts in the currently loaded ski area.
 * @author uqibg
 */
public class ListLiftsCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final char FIELD_SEPARATOR = ' ';

    private static final String ERROR_ARGS = "Error, list lifts takes no arguments";
    private static final String ERROR_NO_AREA = "Error, no area loaded";
    private static final String TRANSIT_SUFFIX = " TRANSIT";

    private final SkiSession session;

    /**
     * Constructor for the ListLiftsCommand.
     * @param session the SkiSession to operate on
     */
    public ListLiftsCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }

        SkiArea area = this.session.getSkiArea();
        if (area == null) {
            System.err.println(ERROR_NO_AREA);
            return;
        }

        List<Lift> lifts = area.getLifts();
        for (Lift lift : lifts) {
            StringBuilder line = new StringBuilder();
            line.append(lift.getId()).append(FIELD_SEPARATOR)
                    .append(lift.getType()).append(FIELD_SEPARATOR)
                    .append(lift.getOpeningTime()).append(FIELD_SEPARATOR)
                    .append(lift.getClosingTime()).append(FIELD_SEPARATOR)
                    .append(lift.getRideDurationMinutes()).append(FIELD_SEPARATOR)
                    .append(lift.getWaitingTimeMinutes());
            if (lift.isTransit()) {
                line.append(TRANSIT_SUFFIX);
            }
            System.out.println(line);
        }
    }
}

