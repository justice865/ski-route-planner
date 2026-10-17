package edu.kit.kastel.view.io;

import edu.kit.kastel.model.parsing.AreaParser;
import edu.kit.kastel.model.parsing.ParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Loads the text file for construction of ski area.
 * @author uqibg
 */
public class FileLoader {
    private static final String ERROR_PATH_NULL = "Error, file path must not be null";
    private static final String ERROR_READ_FILE = "Error, could not read file";
    private static final String ERROR_EMPTY_FILE = "Error, file is empty";

    private final AreaParser parser;

    /**
     * Constructor for FileLoader.
     */
    public FileLoader() {
        this.parser = new AreaParser();
    }

    /**
     * Loads the ski area from the specified file path.
     * @param filePath the path to the ski area file
     * @return the result of the parsing, containing the ski area and any error message if parsing failed
     */
    public ParseResult loadAreaFromFile(String filePath) {
        if (filePath == null) {
            System.err.println(ERROR_PATH_NULL);
            return new ParseResult(null, false, ERROR_PATH_NULL);
        }

        Path path = Paths.get(filePath);
        String fileContent;
        try {
            fileContent = readFileContent(path);
        } catch (FileLoadException e) {
            System.err.println(ERROR_READ_FILE);
            return new ParseResult(null, false, ERROR_READ_FILE);
        }

        if (fileContent.isBlank()) {
            System.err.println(ERROR_EMPTY_FILE);
            return new ParseResult(null, false, ERROR_EMPTY_FILE);
        }

        System.out.println(fileContent);
        ParseResult result = this.parser.parse(path);
        if (!result.success()) {
            System.err.println(result.errorMessage());
        }
        return result;
    }

    private String readFileContent(Path path) throws FileLoadException {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new FileLoadException(ERROR_READ_FILE, e);
        }
    }
}

