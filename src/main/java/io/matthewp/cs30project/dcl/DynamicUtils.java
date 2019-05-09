package io.matthewp.cs30project.dcl;

import lombok.NonNull;

import java.io.File;

/**
 * DynamicUtils
 *
 * This class provides utility functions for the DCL library.
 */
class DynamicUtils {

    /**
     * isValidFile(File)
     *
     * Checks if a file is valid.
     *
     * @param file File to check
     * @return True if file is valid, otherwise false.
     */
    public static boolean isValidFile(@NonNull final File file) {
        final String extension;
        final int i = file.getName().lastIndexOf('.');

        if(i > 0) {
            extension = file.getName().substring(i + 1);
        } else {
            extension = null;
        }

        return extension != null && extension.equalsIgnoreCase("dcl");
    }

    /**
     * isInteger(String)
     *
     * Checks if a string is an integer.
     *
     * @param input Input to check
     * @return True if input is an integer, otherwise false.
     */
    public static boolean isInteger(@NonNull final String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch(NumberFormatException ignored) { }

        return false;
    }

    /**
     * isDouble(String)
     *
     * Checks if a string is an double.
     *
     * @param input Input to check
     * @return True if input is an double, otherwise false.
     */
    public static boolean isDouble(@NonNull final String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch(NumberFormatException ignored) { }

        return false;
    }

    /**
     * replaceLast(String, String, String)
     *
     * Replaces content in a string from the end to the beginning.
     *
     * @param text Text to modify
     * @param regex What to match
     * @param replacement What to replace with
     * @return Modified text
     */
    public static String replaceLast(@NonNull final String text, @NonNull final String regex, @NonNull final String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
