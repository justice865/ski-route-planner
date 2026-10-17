package edu.kit.kastel.model.parsing;

import edu.kit.kastel.model.graph.core.Node;
import edu.kit.kastel.model.graph.core.SkiArea;
import edu.kit.kastel.model.graph.elements.Lift;
import edu.kit.kastel.model.graph.elements.Slope;
import edu.kit.kastel.model.graph.types.Difficulty;
import edu.kit.kastel.model.graph.types.LiftType;
import edu.kit.kastel.model.graph.types.NodeConnectionStatus;
import edu.kit.kastel.model.graph.types.Surface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.util.List;

/**
 * The class that parses ski area.
 * @author uqibg
 */
public class AreaParser {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int TEN = 10;
    private static final int KEEP_EMPTY_SPLIT_PARTS = -1;

    private static final char COLON = ':';
    private static final char DIGIT_ZERO = '0';
    private static final char UNDERSCORE = '_';
    private static final char LOWER_A = 'a';
    private static final char LOWER_Z = 'z';
    private static final char UPPER_A = 'A';
    private static final char UPPER_Z = 'Z';
    private static final char NUMBER_ZERO = '0';
    private static final char NUMBER_NINE = '9';

    private static final String EMPTY = "";
    private static final String HEADER = "graph";
    private static final String EDGE_TOKEN = " --> ";
    private static final String BREAK_TOKEN = "<br/>";
    private static final String FIELD_SEPARATOR = ";";
    private static final String LIFT_OPEN = "[";
    private static final String LIFT_CLOSE = "]";
    private static final String TRANSIT_LIFT_OPEN = "[[";
    private static final String TRANSIT_LIFT_CLOSE = "]]";
    private static final String SLOPE_OPEN = "([";
    private static final String SLOPE_CLOSE = "])";

    private static final String LIFT_TYPE_GONDOLA = "GONDOLA";
    private static final String LIFT_TYPE_CHAIRLIFT = "CHAIRLIFT";
    private static final String DIFFICULTY_BLUE = "BLUE";
    private static final String DIFFICULTY_RED = "RED";
    private static final String DIFFICULTY_BLACK = "BLACK";
    private static final String SURFACE_REGULAR = "REGULAR";
    private static final String SURFACE_BUMPY = "BUMPY";
    private static final String SURFACE_ICY = "ICY";

    private static final int HEAD_AND_FIELDS_PARTS = 2;
    private static final int CONNECTION_PARTS = 2;
    private static final int LIFT_FIELD_COUNT = 5;
    private static final int SLOPE_FIELD_COUNT = 4;
    private static final int TIME_HOUR_TENS_INDEX = 0;
    private static final int TIME_HOUR_ONES_INDEX = 1;
    private static final int TIME_COLON_INDEX = 2;
    private static final int TIME_MINUTE_TENS_INDEX = 3;
    private static final int TIME_MINUTE_ONES_INDEX = 4;

    private static final String ERROR_PATH_NULL = "Error, path must not be null";
    private static final String ERROR_READ_FILE = "Error, could not read area file";
    private static final String ERROR_INVALID_NODE_PREFIX = "Error, invalid node definition at line ";
    private static final String ERROR_INVALID_EDGE_PREFIX = "Error, invalid edge definition at line ";
    private static final String ERROR_MISSING_HEADER = "Error, missing graph header";
    private static final String ERROR_INVALID_LOADED_FILE = "Error, loaded file is not valid";

    private String firstError;

    /**
     * Constructor for AreaParser.
     */
    public AreaParser() {
    }

    /**
     * Parses the ski area from the given file path.
     * @param path the path to the ski area file
     * @return the result of the parsing, containing the ski area and any error message if parsing failed
     */
    public ParseResult parse(Path path) {
        this.firstError = null;
        SkiArea area = new SkiArea();
        if (path == null) {
            return fail(area, ERROR_PATH_NULL);
        }

        final List<String> lines;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            return fail(area, ERROR_READ_FILE);
        }

        boolean hasHeader = false;
        for (int i = ZERO; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            if (HEADER.equals(line)) {
                hasHeader = true;
                continue;
            }
            if (isConnectionDefinition(line)) {
                continue;
            }

            Node node = parseNodeDefinition(line);
            if (node == null) {
                return fail(area, ERROR_INVALID_NODE_PREFIX + (i + ONE));
            }
            area.addNode(node);
        }

        for (int i = ZERO; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (isConnectionDefinition(line)) {
                if (!parseConnection(line, area)) {
                    return fail(area, ERROR_INVALID_EDGE_PREFIX + (i + ONE));
                }
            }
        }

        if (!hasHeader) {
            return fail(area, ERROR_MISSING_HEADER);
        }
        if (!area.isValid()) {
            return fail(area, ERROR_INVALID_LOADED_FILE);
        }
        return new ParseResult(area, true, EMPTY);
    }

    private ParseResult fail(SkiArea area, String message) {
        if (this.firstError == null) {
            this.firstError = message;
        }
        return new ParseResult(area, false, this.firstError);
    }

    private Node parseNodeDefinition(String line) {
        if (isTransitLiftDefinition(line)) {
            return parseLift(line, true);
        }
        if (isLiftDefinition(line)) {
            return parseLift(line, false);
        }
        if (isSlopeDefinition(line)) {
            return parseSlope(line);
        }
        return null;
    }

    private Lift parseLift(String line, boolean transit) {
        String open = transit ? TRANSIT_LIFT_OPEN : LIFT_OPEN;
        String close = transit ? TRANSIT_LIFT_CLOSE : LIFT_CLOSE;

        int openIndex = line.indexOf(open);
        int closeIndex = line.lastIndexOf(close);
        if (openIndex <= ZERO || closeIndex <= openIndex + open.length()) {
            return null;
        }

        String outerId = line.substring(ZERO, openIndex).trim();
        if (!isValidId(outerId)) {
            return null;
        }

        String inner = line.substring(openIndex + open.length(), closeIndex).trim();
        String[] headAndFields = inner.split(BREAK_TOKEN, KEEP_EMPTY_SPLIT_PARTS);
        if (headAndFields.length != HEAD_AND_FIELDS_PARTS) {
            return null;
        }

        String innerId = headAndFields[ZERO].trim();
        if (!outerId.equals(innerId)) {
            return null;
        }

        String[] fields = headAndFields[ONE].split(FIELD_SEPARATOR, KEEP_EMPTY_SPLIT_PARTS);
        if (fields.length != LIFT_FIELD_COUNT) {
            return null;
        }

        LiftType liftType = parseLiftType(fields[ZERO].trim());
        LocalTime openingTime = parseTime(fields[ONE].trim());
        LocalTime closingTime = parseTime(fields[TWO].trim());
        Integer rideDuration = parseInteger(fields[THREE].trim());
        Integer waitingTime = parseInteger(fields[FOUR].trim());

        if (liftType == null || openingTime == null || closingTime == null || rideDuration == null || waitingTime == null) {
            return null;
        }
        if (openingTime.isAfter(closingTime)) {
            return null;
        }

        return new Lift(outerId, liftType, openingTime, closingTime, rideDuration, waitingTime, transit);
    }

    private Slope parseSlope(String line) {
        int openIndex = line.indexOf(SLOPE_OPEN);
        int closeIndex = line.lastIndexOf(SLOPE_CLOSE);
        if (openIndex <= ZERO || closeIndex <= openIndex + TWO) {
            return null;
        }

        String outerId = line.substring(ZERO, openIndex).trim();
        if (!isValidId(outerId)) {
            return null;
        }

        String inner = line.substring(openIndex + TWO, closeIndex).trim();
        String[] headAndFields = inner.split(BREAK_TOKEN, KEEP_EMPTY_SPLIT_PARTS);
        if (headAndFields.length != HEAD_AND_FIELDS_PARTS) {
            return null;
        }

        String innerId = headAndFields[ZERO].trim();
        if (!outerId.equals(innerId)) {
            return null;
        }

        String[] fields = headAndFields[ONE].split(FIELD_SEPARATOR, KEEP_EMPTY_SPLIT_PARTS);
        if (fields.length != SLOPE_FIELD_COUNT) {
            return null;
        }

        Difficulty difficulty = parseDifficulty(fields[ZERO].trim());
        Surface surface = parseSurface(fields[ONE].trim());
        Integer length = parseInteger(fields[TWO].trim());
        Integer elevation = parseInteger(fields[THREE].trim());

        if (difficulty == null || surface == null || length == null || elevation == null) {
            return null;
        }

        return new Slope(outerId, difficulty, surface, length, elevation);
    }

    private boolean parseConnection(String line, SkiArea area) {
        String[] parts = line.split(EDGE_TOKEN, KEEP_EMPTY_SPLIT_PARTS);
        if (parts.length != CONNECTION_PARTS) {
            return false;
        }

        String fromId = parts[ZERO].trim();
        String toId = parts[ONE].trim();
        if (!isValidId(fromId) || !isValidId(toId) || fromId.equals(toId)) {
            return false;
        }

        Node from = area.getNode(fromId);
        Node to = area.getNode(toId);
        if (from == null || to == null) {
            return false;
        }

        NodeConnectionStatus status = area.addConnection(from, to);
        return status != NodeConnectionStatus.REVERSE_SAME_TYPE_NOT_ALLOWED;
    }

    private LocalTime parseTime(String value) {
        if (value.length() != FIVE || value.charAt(TIME_COLON_INDEX) != COLON) {
            return null;
        }
        if (!Character.isDigit(value.charAt(TIME_HOUR_TENS_INDEX))
                || !Character.isDigit(value.charAt(TIME_HOUR_ONES_INDEX))
                || !Character.isDigit(value.charAt(TIME_MINUTE_TENS_INDEX))
                || !Character.isDigit(value.charAt(TIME_MINUTE_ONES_INDEX))) {
            return null;
        }

        int hour = (value.charAt(TIME_HOUR_TENS_INDEX) - DIGIT_ZERO) * TEN
                + (value.charAt(TIME_HOUR_ONES_INDEX) - DIGIT_ZERO);
        int minute = (value.charAt(TIME_MINUTE_TENS_INDEX) - DIGIT_ZERO) * TEN
                + (value.charAt(TIME_MINUTE_ONES_INDEX) - DIGIT_ZERO);
        try {
            return LocalTime.of(hour, minute);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value.isEmpty()) {
            return null;
        }
        for (int i = ZERO; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return null;
            }
        }
        return Integer.parseInt(value);
    }

    private LiftType parseLiftType(String value) {
        if (LIFT_TYPE_GONDOLA.equals(value)) {
            return LiftType.GONDOLA;
        }
        if (LIFT_TYPE_CHAIRLIFT.equals(value)) {
            return LiftType.CHAIRLIFT;
        }
        return null;
    }

    private Difficulty parseDifficulty(String value) {
        if (DIFFICULTY_BLUE.equals(value)) {
            return Difficulty.BLUE;
        }
        if (DIFFICULTY_RED.equals(value)) {
            return Difficulty.RED;
        }
        if (DIFFICULTY_BLACK.equals(value)) {
            return Difficulty.BLACK;
        }
        return null;
    }

    private Surface parseSurface(String value) {
        if (SURFACE_REGULAR.equals(value)) {
            return Surface.REGULAR;
        }
        if (SURFACE_BUMPY.equals(value)) {
            return Surface.BUMPY;
        }
        if (SURFACE_ICY.equals(value)) {
            return Surface.ICY;
        }
        return null;
    }

    private boolean isValidId(String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        for (int i = ZERO; i < id.length(); i++) {
            char c = id.charAt(i);
            boolean isAlphaNum = (c >= LOWER_A && c <= LOWER_Z)
                    || (c >= UPPER_A && c <= UPPER_Z)
                    || (c >= NUMBER_ZERO && c <= NUMBER_NINE);
            if (!isAlphaNum && c != UNDERSCORE) {
                return false;
            }
        }
        return true;
    }

    private boolean isLiftDefinition(String line) {
        return line.contains(LIFT_OPEN)
                && line.contains(LIFT_CLOSE)
                && !line.contains(TRANSIT_LIFT_OPEN)
                && !line.contains(TRANSIT_LIFT_CLOSE)
                && !line.contains(SLOPE_OPEN)
                && !isConnectionDefinition(line);
    }

    private boolean isTransitLiftDefinition(String line) {
        return line.contains(TRANSIT_LIFT_OPEN)
                && line.contains(TRANSIT_LIFT_CLOSE)
                && !isConnectionDefinition(line);
    }

    private boolean isSlopeDefinition(String line) {
        return line.contains(SLOPE_OPEN)
                && line.contains(SLOPE_CLOSE)
                && !isConnectionDefinition(line);
    }

    private boolean isConnectionDefinition(String line) {
        return line.contains(EDGE_TOKEN);
    }
}

