package org.baktra.dtblib;

import java.io.File;

/**
 * Incorrect DOS 2 Binary format
 */
public class DOS2BinaryException extends Exception {

    private final String message;
    private final int offset;
    private final String filename;
    private final boolean isAlien;

    public DOS2BinaryException(String filename, String message, int offset, boolean isAlien) {
        this.message = message;
        this.offset = offset;
        File f = new File(filename);
        this.filename = f.getName();
        this.isAlien = isAlien;
    }

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

    public String toString(boolean full) {
        if (full) {
            return getClass().getName() + " " + getMessageString(true);
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

    public String getFormattedMessage() {
        return getFormattedMessage(null);
    }

    public String getFormattedMessage(String extraHeader) {
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML>");

        if (extraHeader != null) {
            sb.append("<b>");
            sb.append(extraHeader);
            sb.append("</b><BR>");
        }

        sb.append(getClass().getName());
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

    public boolean isAlien() {
        return isAlien;
    }
}
