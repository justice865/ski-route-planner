package edu.kit.kastel.view.command.route;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.planning.SkiSession;
import edu.kit.kastel.view.core.Command;
import java.time.LocalTime;

/**
 * The PlanCommand class represents a command to plan a route in the ski session.
 * @author uqibg
 */
public class PlanCommand extends Command {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int TEN = 10;

    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    private static final char TIME_SEPARATOR = ':';
    private static final char DIGIT_ZERO = '0';

    private static final String ERROR_PLAN_ARGS = "Error, plan requires: <startLiftId> <startTime> <endTime>";
    private static final String ERROR_ROUTE_ACTIVE = "Error, route already active";
    private static final String ERROR_TIME_FORMAT = "Error, invalid time format";
    private static final String ERROR_INTERVAL = "Error, invalid planning interval";
    private static final String ERROR_NO_AREA = "Error, no ski area loaded";
    private static final String ERROR_SKILL_GOAL = "Error, skill and goal must be set";
    private static final String ERROR_START_TRANSIT = "Error, start lift must be a transit lift";
    private static final String ERROR_NO_ROUTE = "Error, no valid route found";
    private static final String MESSAGE_PLANNED = "route planned";

    private final SkiSession session;

    /**
     * Constructor for the PlanCommand.
     * @param session the SkiSession to operate on
     */
    public PlanCommand(SkiSession session) {
        this.session = session;
    }

    @Override
    public void execute(String[] args) {
        if (args.length != THREE) {
            System.err.println(ERROR_PLAN_ARGS);
            return;
        }
        if (this.session.hasActiveRoute()) {
            System.err.println(ERROR_ROUTE_ACTIVE);
            return;
        }

        LocalTime start = parseTime(args[ONE]);
        LocalTime end = parseTime(args[TWO]);
        if (start == null || end == null) {
            System.err.println(ERROR_TIME_FORMAT);
            return;
        }
        if (!start.isBefore(end)) {
            System.err.println(ERROR_INTERVAL);
            return;
        }

        if (!this.session.hasLoadedArea()) {
            System.err.println(ERROR_NO_AREA);
            return;
        }
        if (!this.session.getSkierConfig().isReadyForPlanning()) {
            System.err.println(ERROR_SKILL_GOAL);
            return;
        }

        Node startNode = this.session.getSkiArea().getNode(args[ZERO]);
        if (!(startNode instanceof Lift lift) || !lift.isTransit()) {
            System.err.println(ERROR_START_TRANSIT);
            return;
        }

        this.session.plan(args[ZERO], start, end);
        if (this.session.hasActiveRoute()) {
            System.out.println(MESSAGE_PLANNED);
        } else {
            System.err.println(ERROR_NO_ROUTE);
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.length() != FIVE || value.charAt(TWO) != TIME_SEPARATOR) {
            return null;
        }
        if (!Character.isDigit(value.charAt(ZERO)) || !Character.isDigit(value.charAt(ONE))
                || !Character.isDigit(value.charAt(THREE)) || !Character.isDigit(value.charAt(FOUR))) {
            return null;
        }

        int hour = (value.charAt(ZERO) - DIGIT_ZERO) * TEN + (value.charAt(ONE) - DIGIT_ZERO);
        int minute = (value.charAt(THREE) - DIGIT_ZERO) * TEN + (value.charAt(FOUR) - DIGIT_ZERO);
        if (hour < ZERO || hour >= HOURS_PER_DAY || minute < ZERO || minute >= MINUTES_PER_HOUR) {
            return null;
        }
        return LocalTime.of(hour, minute);
    }
}

