package org.baktra.dtblib;

import java.io.File;

/**
 * Exception thrown when DOS 2 Binary File is corrupt
 */
public class DOS2BinaryException extends Exception {

    private final String message;
    private final int offset;
    private final String filename;
    private final boolean isAlien;
    
    /**Error message prefix*/
    private static final String MSG_PREFIX = "Binary file error";

    /**Create a new DOS2BinaryException
     * 
     * @param filename Input file name
     * @param message Message describing the problem
     * @param offset Offset when the problem was found
     * @param isAlien When true, indicates the input file is not a binary file at all
     */
    public DOS2BinaryException(String filename, String message, int offset, boolean isAlien) {
        this.message = message;
        this.offset = offset;
        File f = new File(filename);
        this.filename = f.getName();
        this.isAlien = isAlien;
    }
    
    /** Create a new DOS2BinaryException
     * 
     * @param filename Input file name
     * @param message Message describing the problem
     * @param offset Offset when the problem was found
     */
    public DOS2BinaryException(String filename, String message, int offset) {
        this(filename, message, offset, false);
    }

    @Override
    public String getMessage() {
        return getMessageString(true);
    }

    @Override
    public String toString() {
        return toString(true);
    }
    
    /**
     * Return string representation of the exception
     * @param full When true, the string includes the message prefix
     * @return String representation of the exception
     */
    public String toString(boolean full) {
        if (full) {
            return MSG_PREFIX+": " + getMessageString(true);
        }
        else {
            return getMessageString(false);
        }
    }
    
    private String getMessageString(boolean full) {
        StringBuilder sb = new StringBuilder();

        if (full) {

            sb.append(filename);
            sb.append(": ");
        }
        sb.append(message);
        if (!message.endsWith(".")) {
            sb.append('.');
        }
        sb.append(" Offset: ");
        sb.append(offset);
        sb.append(" $");
        sb.append(Integer.toHexString(offset).toUpperCase());
        return sb.toString();
    }
    
    /**
     * Get message formatted as HTML
     * @return String with HTML formatted message
     */
    public String getFormattedMessage() {
        return getFormattedMessage(null);
    }

    /**
     * Get message formatted as HTML with a custom header
     * @param customHeader Custom header
     * @return String with HTML formatted message and a custom header
     */
    public String getFormattedMessage(String customHeader) {
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML>");

        if (customHeader != null) {
            sb.append("<b>");
            sb.append(customHeader);
            sb.append("</b><BR>");
        }

        sb.append(MSG_PREFIX);
        sb.append(": ");
        sb.append(filename != null ? filename : "<No file>");
        sb.append("<BR>");
        sb.append(message);
        if (!message.endsWith(".")) {
            sb.append('.');
        }
        sb.append("<BR>");
        sb.append(" Offset: ");
        sb.append(String.format("%05d", offset));
        sb.append(" $");
        sb.append(String.format("%04X", offset));
        sb.append("</HTML>");
        return sb.toString();
    }
    
    /**
     * Returns true if the exception represents a file that is not a binary file
     * @return True when not a binary file
     */
    public boolean isAlien() {
        return isAlien;
    }
}
