package edu.kit.kastel.view.core;

import edu.kit.kastel.model.planning.SkiSession;

/**
 * Factory for creating commands.
 * @author uqibg
 */
@FunctionalInterface
public interface CommandFactory {
    /**
     * Creates a command.
     * @param handler the CommandHandler to which the command will be registered
     * @param session the SkiSession to operate on
     * @return the created Command
     */
    Command create(CommandHandler handler, SkiSession session);
}

