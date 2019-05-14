package io.matthewp.cs30project.dcl;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * DynamicLoader
 *
 * Handles the parsing of all ".dcl" configuration files.
 *
 * TODO: Implement variables (see {@link DynamicLoader})
 * TODO: Implement string lists and integer lists (but use only [ and ] instead of [s and s], etc)
 */
public final class DynamicLoader {
    private static final boolean DEBUG = DynamicConfiguration.DEBUG;
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u(\\p{XDigit}{4})");

    @Getter private final File file;
    @Getter private DynamicSection root;
    @Getter private Map<String, DynamicSection> sections;

    /**
     * DynamicLoader(File)
     *
     * Creates a new {@link DynamicLoader} instance.
     *
     * @param file File
     */
    @SneakyThrows(IOException.class)
    DynamicLoader(@NonNull final File file) {
        this.file = file;
        this.root = new DynamicSection("");
        this.sections = new LinkedHashMap<>();

        // currentSection stores the section we are currently adding values to.
        DynamicSection currentSection = this.root;

        // Create a Buffered Reader so we can read from the file.
        final BufferedReader br = new BufferedReader(new FileReader(file));

        // lineNumber stores what line we are currently on.
        int lineNumber = 0;

        // Loop through every line in the file.
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            // Increment the line number.
            lineNumber++;
            // Normalize the line read from the file.
            line = this.normalize(line);

            // Debug logging.
            if(DEBUG) {
                System.out.println("[DCL] Line: '" + line + "'");
            }

            // Check if the line is empty.
            if(line.length() < 1) {
                // Debug logging.
                if(DEBUG) {
                    System.out.println("[DCL] Skipping line #" + lineNumber + " because it is empty.");
                }

                // Continue to the next line.
                continue;
            }

            // Check if the line is a comment.
            if(this.isComment(line)) {
                // Debug logging.
                if(DEBUG) {
                    System.out.println("[DCL] Skipping line #" + lineNumber + " because it is a comment.");
                }

                // Continue to the next line.
                continue;
            }

            // Check if the line is the start of a section.
            if(this.isSectionStart(line)) {
                // Debug logging.
                if(DEBUG) {
                    System.out.println("[DCL] New section on line #" + lineNumber + ".");
                }

                // sectionName gets the current section name and appends the new section name to it.
                final String sectionName = (
                        (currentSection.getKey().length() == 0) ? "" : currentSection.getKey() + "."
                ) + line.substring(0, line.length() - 2);

                // Check if our section map does not have a section with that name.
                if(!this.getSections().containsKey(sectionName)) {
                    // Create a new section.
                    currentSection = new DynamicSection(sectionName, lineNumber);

                    // Add the new section to the sections map.
                    this.getSections().put(sectionName, currentSection);
                } else {
                    // Update the current section, the section already exists in the map.
                    currentSection = this.getSections().get(sectionName);
                }

                // Continue to the next line.
                continue;
            }

            // Check if the line is a section ending.
            if(currentSection != this.getRoot() && this.isSectionEnd(line)) {
                // Debug logging.
                if(DEBUG) {
                    System.out.println("[DCL] Section end on line #" + lineNumber + ".");
                }

                currentSection.setEndLineNumber(lineNumber);
                if(DEBUG) {
                    System.out.println("[DCL] " + currentSection.getKey() + " - Set end line number to: " + lineNumber);
                }

                // Get the current section name.
                String sectionName = currentSection.getKey();

                // Check if the section name contains a "." character.
                if(sectionName.contains(".")) {
                    // Remove everything on and after the last "." in the string (back up one section)
                    // For example, "section.section" would become "section".
                    sectionName = sectionName.substring(0, sectionName.lastIndexOf("."));

                    // Update our current section.
                    currentSection = this.getSections().get(sectionName);
                } else {
                    // Our section is not embedded within another section, set it to the root section.
                    currentSection = this.getRoot();
                }

                // Continue to the next line.
                continue;
            }

            // After we have ran the rest of our checks we know the line is probably a value.
            this.parseValue(currentSection, line, lineNumber);
        }
    }

    private void parseValue(@NonNull final DynamicSection section, @NonNull final String line, final int lineNumber) {
        String name = null;
        String value = null;

        for(int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            if(character != ':') {
                continue;
            }

            if(line.charAt(i + 1) != ' ') {
                if(DEBUG) {
                    System.out.println("[DCL] Missing space after ':' in value on line #" + lineNumber + ".");
                }
                break;
            }

            name = line.substring(0, i);
            value = line.substring(i + 2);
            if(DEBUG) {
                System.out.println("[DCL] Found value at line #" + lineNumber + " with name '" + name + "'.");
                System.out.println("- " + value);
            }
        }

        // Check if the name is null, this also means there is no value.
        if(name == null) {
            if(DEBUG) {
                System.out.println("[DCL] Failed to find name or value for line #" + lineNumber + ".");
            }

            return;
        }

        // Add the DynamicValue to the section.
        section.addValue(name, this.getValue(value, lineNumber));
    }

    /**
     * normalize(String)
     *
     * Normalizes a string by removing prefixing and trailing white space.
     *
     * @param input Input
     * @return Normalized input
     */
    private String normalize(@NonNull final String input) {
        return input.trim();
    }

    /**
     * isComment(String)
     *
     * Checks if a string is a comment.
     *
     * @param input Input
     * @return True if the line is a comment, otherwise false.
     */
    private boolean isComment(@NonNull final String input) {
        if(input.length() < 2) {
            return false;
        }

        final String substring = input.substring(0, 2);
        return substring.equals("//") || substring.equals("# ");
    }

    /**
     * isSectionStart(String)
     *
     * Checks if a string is the start of a section.
     *
     * @param input Input
     * @return True if the line is a section start, otherwise false.
     */
    private boolean isSectionStart(@NonNull final String input) {
        if(input.length() < 2) {
            return false;
        }

        return input.substring(input.length() - 2).equals(" {");
    }

    /**
     * isSectionEnd(String)
     *
     * Checks if a string is the end of a section.
     *
     * @param input Input
     * @return True if the line is a section ending, otherwise false.
     */
    private boolean isSectionEnd(@NonNull final String input) {
        return input.charAt(0) == '}';
    }

    /**
     * getValue(String, int)
     *
     * Converts a string into a {@link DynamicValue} with a proper type.
     *
     * @param input Input
     * @param lineNumber Line Number
     * @return {@link DynamicValue} object.
     */
    private DynamicValue getValue(@NonNull final String input, final int lineNumber) {
        if(input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"') {
            return new DynamicValue(input.substring(1, input.length() - 1), DynamicValue.ValueType.STRING, lineNumber);
        }

        if(input.equals("true") || input.equals("false")) {
            return new DynamicValue(Boolean.valueOf(input), DynamicValue.ValueType.BOOLEAN, lineNumber);
        }

        if(DynamicUtils.isInteger(input)) {
            return new DynamicValue(Integer.valueOf(input), DynamicValue.ValueType.INTEGER, lineNumber);
        }

        if(DynamicUtils.isDouble(input)) {
            return new DynamicValue(Double.valueOf(input), DynamicValue.ValueType.DOUBLE, lineNumber);
        }

        return new DynamicValue(input, DynamicValue.ValueType.STRING, lineNumber);
    }
}
