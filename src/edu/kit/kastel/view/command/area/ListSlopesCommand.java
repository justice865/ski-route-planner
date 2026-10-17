package edu.kit.kastel.view.command.area;

import edu.kit.kastel.model.graph.core.SkiArea;
import edu.kit.kastel.model.graph.elements.Slope;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import java.util.List;

/**
 * Command to list the slopes of the currently loaded ski area.
 * @author uqibg
 */
public class ListSlopesCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final char FIELD_SEPARATOR = ' ';

    private static final String ERROR_ARGS = "Error, list slopes takes no arguments";
    private static final String ERROR_NO_AREA = "Error, no area loaded";

    private final SkiSession session;

    /**
     * Constructor for the ListSlopesCommand.
     * @param session the SkiSession to operate on
     */
    public ListSlopesCommand(SkiSession session) {
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

        List<Slope> slopes = area.getSlopes();
        for (Slope slope : slopes) {
            StringBuilder line = new StringBuilder();
            line.append(slope.getId()).append(FIELD_SEPARATOR)
                    .append(slope.getDifficulty()).append(FIELD_SEPARATOR)
                    .append(slope.getSurface()).append(FIELD_SEPARATOR)
                    .append(slope.getLengthMeters()).append(FIELD_SEPARATOR)
                    .append(slope.getElevationMeters());
            System.out.println(line);
        }
    }
}

