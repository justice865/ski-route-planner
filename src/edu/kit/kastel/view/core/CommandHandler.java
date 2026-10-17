package edu.kit.kastel.view.core;

import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.command.area.ListLiftsCommand;
import edu.kit.kastel.view.command.area.ListSlopesCommand;
import edu.kit.kastel.view.command.area.LoadAreaCommand;
import edu.kit.kastel.view.command.preference.DislikeCommand;
import edu.kit.kastel.view.command.preference.LikeCommand;
import edu.kit.kastel.view.command.preference.ResetPreferencesCommand;
import edu.kit.kastel.view.command.preference.SetGoalCommand;
import edu.kit.kastel.view.command.preference.SetSkillCommand;
import edu.kit.kastel.view.command.route.AbortCommand;
import edu.kit.kastel.view.command.route.AlternativeCommand;
import edu.kit.kastel.view.command.route.NextCommand;
import edu.kit.kastel.view.command.route.PlanCommand;
import edu.kit.kastel.view.command.route.ShowRouteCommand;
import edu.kit.kastel.view.command.route.TakeCommand;
import edu.kit.kastel.view.command.system.QuitCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The class that handles the user input and executes the corresponding commands.
 * @author uqibg
 */
public class CommandHandler {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String SPACE = " ";

    private static final String ERROR_UNKNOWN_COMMAND = "Error, unknown command";

    private static final String CMD_LOAD_AREA = "load area";
    private static final String CMD_SET_SKILL = "set skill";
    private static final String CMD_SET_GOAL = "set goal";
    private static final String CMD_LIKE = "like";
    private static final String CMD_DISLIKE = "dislike";
    private static final String CMD_RESET_PREFERENCES = "reset preferences";
    private static final String CMD_PLAN = "plan";
    private static final String CMD_NEXT = "next";
    private static final String CMD_TAKE = "take";
    private static final String CMD_ALTERNATIVE = "alternative";
    private static final String CMD_ABORT = "abort";
    private static final String CMD_SHOW_ROUTE = "show route";
    private static final String CMD_LIST_LIFTS = "list lifts";
    private static final String CMD_LIST_SLOPES = "list slopes";
    private static final String CMD_QUIT = "quit";

    private final SkiSession session;
    private final Map<String, CommandFactory> commandFactories;
    private boolean quitRequested;

    /**
     * Constructor for the CommandHandler.
     */
    public CommandHandler() {
        this.session = new SkiSession();
        this.commandFactories = new HashMap<>();
        this.quitRequested = false;
        initializeCommands();
    }

    /**
     * Requests the quitting for the program.
     */
    public void requestQuit() {
        this.quitRequested = true;
    }

    /**
     * Reads the user input and executes the corresponding commands until the quitting is requested.
     */
    public void readCommands() {
        Scanner scanner = new Scanner(System.in);
        while (!this.quitRequested && scanner.hasNextLine()) {
            handleCommand(scanner.nextLine());
        }
    }

    /**
     * Handles a single input line.
     * @param line the input line to handle
     */
    public void handleCommand(String line) {
        if (line == null || line.trim().isEmpty()) {
            return;
        }

        String[] tokens = line.trim().split(WHITESPACE_REGEX);
        if (tokens.length == ZERO) {
            return;
        }

        String commandName = findCommandName(tokens);
        if (commandName == null) {
            System.err.println(ERROR_UNKNOWN_COMMAND);
            return;
        }

        CommandFactory factory = this.commandFactories.get(commandName);
        if (factory == null) {
            System.err.println(ERROR_UNKNOWN_COMMAND);
            return;
        }

        Command command = factory.create(this, this.session);
        String[] args = extractArgs(tokens, commandName);
        command.execute(args);
    }

    private void initializeCommands() {
        commandFactories.put(CMD_LOAD_AREA, (handler, session) -> new LoadAreaCommand(session));
        commandFactories.put(CMD_SET_SKILL, (handler, session) -> new SetSkillCommand(session));
        commandFactories.put(CMD_SET_GOAL, (handler, session) -> new SetGoalCommand(session));
        commandFactories.put(CMD_LIKE, (handler, session) -> new LikeCommand(session));
        commandFactories.put(CMD_DISLIKE, (handler, session) -> new DislikeCommand(session));
        commandFactories.put(CMD_RESET_PREFERENCES, (handler, session) -> new ResetPreferencesCommand(session));
        commandFactories.put(CMD_PLAN, (handler, session) -> new PlanCommand(session));
        commandFactories.put(CMD_NEXT, (handler, session) -> new NextCommand(session));
        commandFactories.put(CMD_TAKE, (handler, session) -> new TakeCommand(session));
        commandFactories.put(CMD_ALTERNATIVE, (handler, session) -> new AlternativeCommand(session));
        commandFactories.put(CMD_ABORT, (handler, session) -> new AbortCommand(session));
        commandFactories.put(CMD_SHOW_ROUTE, (handler, session) -> new ShowRouteCommand(session));
        commandFactories.put(CMD_LIST_LIFTS, (handler, session) -> new ListLiftsCommand(session));
        commandFactories.put(CMD_LIST_SLOPES, (handler, session) -> new ListSlopesCommand(session));
        commandFactories.put(CMD_QUIT, (handler, session) -> new QuitCommand(handler));
    }

    private String findCommandName(String[] tokens) {
        if (tokens.length >= TWO) {
            String twoWord = (tokens[ZERO] + SPACE + tokens[ONE]).toLowerCase();
            if (this.commandFactories.containsKey(twoWord)) {
                return twoWord;
            }
        }
        String oneWord = tokens[ZERO].toLowerCase();
        if (this.commandFactories.containsKey(oneWord)) {
            return oneWord;
        }
        return null;
    }

    private String[] extractArgs(String[] tokens, String commandName) {
        int commandTokens = commandName.split(WHITESPACE_REGEX).length;
        if (tokens.length <= commandTokens) {
            return new String[ZERO];
        }
        String[] args = new String[tokens.length - commandTokens];
        System.arraycopy(tokens, commandTokens, args, ZERO, args.length);
        return args;
    }
}

