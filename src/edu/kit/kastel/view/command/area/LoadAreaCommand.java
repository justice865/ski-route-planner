package edu.kit.kastel.view.command.area;

import edu.kit.kastel.model.parsing.ParseResult;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import edu.kit.kastel.view.io.FileLoader;

/**
 * The LoadAreaCommand class represents a command to load a ski area from a file.
 * @author uqibg
 */
public class LoadAreaCommand extends Command {
    private static final int ONE_ARG = 1;
    private static final int FIRST_ARG_INDEX = 0;

    private static final String ERROR_ARGS = "Error, load command requires exactly one argument";

    private final SkiSession session;
    private final FileLoader fileLoader;

    /**
     * Constructor for the LoadAreaCommand.
     * @param session the SkiSession to operate on
     */
    public LoadAreaCommand(SkiSession session) {
        this.session = session;
        this.fileLoader = new FileLoader();
    }

    @Override
    public void execute(String[] args) {
        if (args.length != ONE_ARG) {
            System.err.println(ERROR_ARGS);
            return;
        }

        ParseResult result = this.fileLoader.loadAreaFromFile(args[FIRST_ARG_INDEX]);
        if (result.success() && result.area() != null) {
            this.session.loadArea(result.area());
        }
    }
}
