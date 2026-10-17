package edu.kit.kastel.view.command.preference;

import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;

/**
 * The Command that is specified to reset the preferences of the current session.
 * @author uqibg
 */
public class ResetPreferencesCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final String ERROR_ARGS = "Error, reset preferences takes no arguments";

    private final SkiSession session;
/**
     * Constructor for the ResetPreferencesCommand.
     * @param session the SkiSession to operate on
     */
    public ResetPreferencesCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }
        this.session.resetPreferences();
    }
}

