package edu.kit.kastel.view.core;

/**
 * The main class that is the entry point of the application.
 * @author uqibg
 */
public final class Main {
    private Main() {
    }

    /**
     * The main method that starts the application.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CommandHandler handler = new CommandHandler();
        handler.readCommands();
    }
}

