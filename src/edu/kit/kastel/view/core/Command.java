package edu.kit.kastel.view.core;

/**
 * The abstract Command class represents a command that can be executed.
 * @author uqibg
 */
public abstract class Command {
    /**
     * Executes the command.
     * @param args the arguments for the command
     */
    public abstract void execute(String[] args);
}

