package edu.kit.kastel.view.command.system;

import edu.kit.kastel.view.core.Command;
import edu.kit.kastel.view.core.CommandHandler;

/**
 * The QuitCommand class represents the command to quit the application.
 * @author uqibg
 */
public class QuitCommand extends Command {
    private static final int NO_ARGS = 0;

    private static final String ERROR_ARGS = "Error, quit takes no arguments";

    private final CommandHandler handler;

    /**
     * Constructor for the QuitCommand.
     * @param handler the CommandHandler to request quit from
     */
    public QuitCommand(CommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != NO_ARGS) {
            System.err.println(ERROR_ARGS);
            return;
        }
        this.handler.requestQuit();
    }
}

