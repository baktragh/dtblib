package org.baktra.dtblib;

/**
 * DOS2 Binary processing exception
 */
public class DOS2BinaryProcessingException extends Exception {

    /**
     * Message
     */
    private final String message;

    /**
     * Create new DOS2BinaryProcessingException
     * @param message
     */
    public DOS2BinaryProcessingException(String message) {
        this.message = message;

    }

   
    @Override
    public String getMessage() {
        return getMessageString();
    }

   
    @Override
    public String toString() {
        return "Binary file processing error: " + " " + getMessageString();
    }

    private String getMessageString() {
        return message;
    }
}
