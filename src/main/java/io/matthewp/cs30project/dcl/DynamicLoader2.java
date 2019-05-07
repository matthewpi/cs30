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
 * DynamicLoader2
 *
 * Version 2 of the {@link DynamicLoader}.
 *
 * TODO: Implement variables (see {@link DynamicLoader})
 * TODO: Implement string lists and integer lists (but use only [ and ] instead of [s and s], etc)
 * TODO: Document code
 */
public final class DynamicLoader2 {
    private static final boolean DEBUG = DynamicConfiguration.DEBUG;
    private static final Pattern UNICODE_PATTERN = Pattern.compile("\\\\u(\\p{XDigit}{4})");

    @Getter private final File file;
    @Getter private DynamicSection root;
    @Getter private Map<String, DynamicSection> sections;

    @SneakyThrows(IOException.class)
    DynamicLoader2(@NonNull final File file) {
        this.file = file;
        this.root = new DynamicSection("");
        this.sections = new LinkedHashMap<>();

        DynamicSection currentSection = this.root;

        // Create a Buffered Reader so we can read from the file.
        final BufferedReader br = new BufferedReader(new FileReader(file));

        // Create a variable to store what line number we are on.
        int lineNumber = 0;

        // Loop through every line in the file.
        for(String line = br.readLine(); line != null; line = br.readLine()) {
            lineNumber++;
            line = this.normalize(line);
            System.out.println("Line: '" + line + "'");

            if(line.length() < 1) {
                if(DEBUG) {
                    System.out.println("[DCL] Skipping line #" + lineNumber + " because it is empty.");
                }

                continue;
            }

            // Check if the line is a comment.
            if(this.isComment(line)) {
                if(DEBUG) {
                    System.out.println("[DCL] Skipping line #" + lineNumber + " because it is a comment.");
                }

                continue;
            }

            // Check if the line is the start of a section.
            if(this.isSectionStart(line)) {
                if(DEBUG) {
                    System.out.println("[DCL] New section on line #" + lineNumber + ".");
                }

                final String sectionName = ((currentSection.getKey().length() == 0) ? "" : currentSection.getKey() + ".") + line.substring(0, line.length() - 2);
                currentSection = (this.getSections().get(sectionName) == null) ? new DynamicSection(sectionName, lineNumber) : this.getSections().get(sectionName);
                this.getSections().putIfAbsent(sectionName, currentSection);
                continue;
            }

            // Check if the line is a section ending.
            if(currentSection != this.getRoot() && this.isSectionEnd(line)) {
                if(DEBUG) {
                    System.out.println("[DCL] Section end on line #" + lineNumber + ".");
                }

                String sectionName = currentSection.getKey();
                if(sectionName.contains(".")) {
                    final String[] split = sectionName.split("\\.");
                    sectionName = DynamicUtils.replaceLast(sectionName, "." + split[split.length - 1], "");
                } else {
                    sectionName = "";
                }

                if(sectionName.length() < 1) {
                    currentSection = this.getRoot();
                } else {
                    currentSection = this.getSections().get(sectionName);
                }
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
                System.out.println("[DCL] Missing space after ':' in value on line #" + lineNumber + ".");
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
     * Normalizes a string by removing prefixing and trailing white space.
     * @param input Input
     * @return Normalized input
     */
    private String normalize(@NonNull final String input) {
        return input.trim();
    }

    private boolean isComment(@NonNull final String input) {
        if(input.length() < 2) {
            return false;
        }

        final String substring = input.substring(0, 2);
        return substring.equals("//") || substring.equals("# ");
    }

    private boolean isSectionStart(@NonNull final String input) {
        if(input.length() < 2) {
            return false;
        }

        return input.substring(input.length() - 2).equals(" {");
    }

    private boolean isSectionEnd(@NonNull final String input) {
        return input.charAt(0) == '}';
    }

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
