package io.matthewp.cs30project.dcl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * DynamicConfiguration
 *
 * This class provides the main functions and handlers required to use a ".dcl" configuration.
 */
public abstract class DynamicConfiguration {
    public static final boolean DEBUG = true;

    @Getter(AccessLevel.PACKAGE) private final File file;
    @Getter(AccessLevel.PACKAGE) private final DynamicLoader loader;
    @Getter(AccessLevel.PACKAGE) private final DynamicSection root;
    @Getter(AccessLevel.PACKAGE) private final Map<String, DynamicSection> sections;

    /**
     * DynamicConfiguration(String)
     *
     * Creates a new {@link DynamicConfiguration} object.
     *
     * @param fileName File name
     */
    @SneakyThrows
    public DynamicConfiguration(@NonNull final String fileName) {
        final File directory = new File(System.getProperty("user.dir"));
        final File file = new File(directory + File.separator + fileName);

        // Check if the file is invalid.
        if(!DynamicUtils.isValidFile(file)) {
            throw new IllegalArgumentException("File \"" + file.getName() + "\" is not a valid file.");
        }

        // Check if the directory exists.
        if(!directory.isDirectory()) {
            // Create the directories.
            if(!directory.mkdirs()) {
                throw new IllegalStateException("Failed to create directory.");
            }
        }

        // Check if the file doesn't exist.
        if(!file.exists()) {
            // Create a new input stream.
            final InputStream in = this.getClass().getClassLoader().getResourceAsStream(fileName);

            // Check if the input stream is null.
            if(in == null) {
                throw new IllegalStateException("Failed to create an input stream.");
            }

            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

            in.close();
        }

        this.file = file;
        this.loader = new DynamicLoader(file);
        this.root = this.getLoader().getRoot();
        this.sections = this.getLoader().getSections();
    }

    /**
     * getString(String)
     *
     * Gets a {@link String}.
     *
     * @param key Key
     * @return Found string or null.
     */
    public String getString(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asString() : null;
    }

    /**
     * getBoolean(String)
     *
     * Gets a {@link Boolean}.
     *
     * @param key Key
     * @return Found boolean or null.
     */
    public Boolean getBoolean(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asBoolean() : null;
    }

    /**
     * getInteger(String)
     *
     * Gets a {@link Integer}.
     *
     * @param key Key
     * @return Found integer or null.
     */
    public Integer getInteger(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asInteger() : null;
    }

    /**
     * getDouble(String)
     *
     * Gets a {@link Double}.
     *
     * @param key Key
     * @return Found double or null.
     */
    public Double getDouble(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asDouble() : null;
    }

    /**
     * getStringList(String)
     *
     * Gets a {@link List<String>}.
     *
     * @param key Key
     * @return Found string list or null.
     */
    public List<String> getStringList(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asStringList() : null;
    }

    /**
     * getIntegerList(String)
     *
     * Gets a {@link List<Integer>}.
     *
     * @param key Key
     * @return Found integer list or null.
     */
    public List<Integer> getIntegerList(@NonNull final String key) {
        final DynamicValue value = this.getValue(key);
        return (value != null) ? value.asIntegerList() : null;
    }

    /**
     * getSection(String)
     *
     * Gets a {@link DynamicSection}.
     *
     * @param key Key
     * @return Found section or null.
     */
    public DynamicSection getSection(@NonNull final String key) {
        if(key.length() == 0) {
            return null;
        }

        return this.getSections().get(key);
    }

    /**
     * set(String, Object)
     *
     * Updates a value's value.
     *
     * @param key Key
     * @param newValue New Value
     */
    public void set(@NonNull final String key, @NonNull final Object newValue) {
        DynamicValue value = this.getValue(key);

        if(value == null) {
            // TODO: Create new value add update all line numbers after it.
            int lineNumber;
            final DynamicSection section;

            if(key.contains(".")) {
                section = this.getSection(key.substring(0, key.lastIndexOf(".")));
            } else {
                section = this.getRoot();
            }

            if(section == null) {
                // Debug logging.
                if(DEBUG) {
                    System.out.println("[DCL] Cannot create new value, section does not exist.");
                }

                return;
            }

            lineNumber = this.getTailLineNumber(section.getValues(), section.getLineNumber());
            if(DEBUG) {
                System.out.println("[DCL] set(): " + lineNumber);
            }

            // Increment the line number because it is an existing line, not the line we need to place the new value on.
            lineNumber++;

            final int finalLine = lineNumber;

            // Loop through all root values.
            this.getRoot().getValues().values().forEach((val) -> {
                if(val.getLineNumber() < finalLine) {
                    return;
                }

                val.setModified(true);
                val.setLineNumber(val.getLineNumber() + 1);
            });

            this.getSections().forEach((sectName, sect) -> {
                if(sect.getLineNumber() >= finalLine) {
                    if(DEBUG) {
                        System.out.println(sectName + ": " + sect.getEndLineNumber());
                    }
                    sect.setModified(true);
                    sect.setLineNumber(sect.getLineNumber() + 1);
                    sect.setEndLineNumber(sect.getEndLineNumber() + 1);
                } else if(sect.getEndLineNumber() >= finalLine) {
                    sect.setModified(true);
                    sect.setEndLineNumber(sect.getEndLineNumber() + 1);
                }

                sect.getValues().values().forEach((val) -> {
                    if(val.getLineNumber() < finalLine) {
                        return;
                    }

                    val.setModified(true);
                    val.setLineNumber(val.getLineNumber() + 1);
                });
            });

            value = new DynamicValue(newValue, null, lineNumber);
            value.setModified(true);

            if(key.contains(".")) {
                section.getValues().put(key.substring(key.lastIndexOf(".") + 1), value);
            } else {
                section.getValues().put(key, value);
            }

            return;
        }

        value.set(newValue);
    }

    private int getTailLineNumber(@NonNull final Map<String, DynamicValue> map, final int sectionLineNumber) {
        int lineNumber = 0;

        System.out.println("[DCL] Map Size: " + map.size());

        if(map.size() == 0) {
            return sectionLineNumber;
        }

        try {
            // We are using reflection to access the tail of the map, this is stored by a LinkedHashMap but not publicly accessible.
            final Field field = map.getClass().getDeclaredField("tail");
            field.setAccessible(true);

            // This line could probably be a lot safer and could potentially throw a NullPointerException.
            final Map.Entry<String, DynamicValue> value = (Map.Entry<String, DynamicValue>) field.get(map);
            lineNumber = value.getValue().getLineNumber();
        } catch(final Exception ignored) {
            // Fall back for getting the last item in an array/set.
            for(final DynamicValue val : map.values()) {
                lineNumber = val.getLineNumber();
            }
        }

        return lineNumber;
    }

    /**
     * getValue(String)
     *
     * Gets a {@link DynamicValue}.
     *
     * @param key Key
     * @return Found value or null.
     */
    private DynamicValue getValue(@NonNull final String key) {
        if(key.length() == 0) {
            return null;
        }

        if(key.contains(".")) {
            final String[] split = key.split("\\.");

            if(split.length >= 2) {
                final String value = split[split.length - 1];
                final String newKey = DynamicUtils.replaceLast(key, "." + value, "");
                final DynamicSection section = this.getSection(newKey);

                if(section != null) {
                    final DynamicValue finalValue = section.get(value);

                    if(finalValue != null) {
                        return finalValue;
                    }
                }
            }
        }

        return this.getRoot().get(key);
    }

    public void debug() {
        this.getRoot().getValues().keySet().forEach(key -> System.out.println(key + ": " + this.getRoot().getValues().get(key).value()));

        if(this.getRoot().getValues().size() != 0) {
            System.out.println();
        }

        for(DynamicSection section : this.getSections().values()) {
            if(section.getValues().size() == 0) {
                continue;
            }

            final String indent = "    ";
            final String sectionIndent = indent.replaceFirst(" {4}", "");
            System.out.println(sectionIndent + section.getKey() + " {");

            for(String valueKey : section.getValues().keySet()) {
                System.out.println(indent + valueKey + ": " + section.getValues().get(valueKey).value());
            }

            System.out.println(sectionIndent + "}");
            System.out.println();
        }
    }

    /**
     * save()
     *
     * Saves the configuration file with any modified values.
     *
     * @throws IOException File Exception
     */
    public void save() throws IOException {
        // Create a map to store the lines we need to replace. (We use a LinkedHashMap because order matters)
        final Map<Integer, String> replacements = new LinkedHashMap<>();

        // Loop through all root values.
        this.getRoot().getValues().forEach((key, value) -> {
            // Skip over values that have not been modified.
            if(!value.isModified()) {
                return;
            }

            // Update the value's modified state so we don't keep saving it.
            value.setModified(false);

            // Put the new line in the replacements list.
            replacements.put(value.getLineNumber(), key + ": " + ((value.getType() == DynamicValue.ValueType.STRING) ? "\"" + value.value() + "\"" : value.value()));
        });

        // Loop through all configuration sections.
        this.getSections().forEach((sectionName, section) -> {
            if(section.isModified()) {
                final String indent1 = String.join(
                        "",
                        Collections.nCopies(
                                (sectionName.length() - sectionName.replace(".", "").length()),
                                "    "
                        )
                );

                replacements.put(section.getLineNumber() - 1, "");
                replacements.put(section.getLineNumber(), indent1 + sectionName + " {");
                replacements.put(section.getEndLineNumber(), indent1 + "}");
                if(DEBUG) {
                    System.out.println("Section End: " + section.getEndLineNumber());
                }
                section.setModified(false);
            }

            section.getValues().forEach((key, value) -> {
                // Skip over values that have not been modified.
                if(!value.isModified()) {
                    return;
                }

                // Update the value's modified state so we don't keep saving it.
                value.setModified(false);

                // Skip over list types because our save handler only supports single line values.
                if(value.getType() == DynamicValue.ValueType.STRING_LIST || value.getType() == DynamicValue.ValueType.INTEGER_LIST) {
                    return;
                }

                // Get the indent for the line. (This is a fancy way of repeating a string, there is probably a better way to do this.)
                final String indent = String.join(
                        "",
                        Collections.nCopies(
                                (sectionName.length() - sectionName.replace(".", "").length()) + 1,
                                "    "
                        )
                );

                // Get the value part of the line, add quotes if the type is a string.
                final String lineValue = ((value.getType() == DynamicValue.ValueType.STRING) ? "\"" + value.value() + "\"" : value.value());

                // Put the new line in the replacements list.
                replacements.put(value.getLineNumber(), indent + key + ": " + lineValue);
            });
        });

        // Check if there are no replacements.
        if(replacements.size() < 1) {
            if(DEBUG) {
                System.out.println("[DCL] Skipping over file save because no values were modified.");
            }
            return;
        }

        // Get the path for our file due to the argument Files#readAllLines takes.
        final Path path = Paths.get(this.getFile().getPath());

        // Get an array of the lines from our config file.
        final List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        // Loop through our replacements.
        replacements.forEach((key, value) -> {
            // Debug logging.
            if(DEBUG) {
                System.out.println("[DCL] Replacing line #" + key);
            }

            // Update the line in the lines array (key - 1 is because a list index starts at 0 instead of 1).
            if(key - 1 >= lines.size()) {
                while(lines.size() < key) {
                    lines.add("");
                }

                lines.set(key - 1, value);
            } else {
                lines.set(key - 1, value);
            }
        });

        // Update the file.
        // TODO: Write to actual config file and not a test file.
        Files.write(Paths.get(this.getFile().getPath() + ".updated"), lines, StandardCharsets.UTF_8);
    }
}
