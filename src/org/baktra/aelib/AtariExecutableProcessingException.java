package org.baktra.aelib;

/**
 * DOS2 Binary processing exception
 */
public class AtariExecutableProcessingException extends Exception {

    /**
     * Message
     */
    private final String message;

    /**
     * Create new DOS2BinaryProcessingException
     * @param message
     */
    public AtariExecutableProcessingException(String message) {
        this.message = message;

    }

   
    @Override
    public String getMessage() {
        return getMessageString();
    }

   
    @Override
    public String toString() {
        return "Atari executable processing error: " + " " + getMessageString();
    }

    private String getMessageString() {
        return message;
    }
}
