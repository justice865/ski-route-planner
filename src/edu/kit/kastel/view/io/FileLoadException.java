package edu.kit.kastel.view.io;

/**
 * The FileLoadException class represents an exception that occurs when loading a file fails.
 * @author uqibg
 */
public class FileLoadException extends Exception {
    /**
     * Constructor for the FileLoadException.
     * @param message the error message
     * @param cause the cause of the exception
     */
    public FileLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}


