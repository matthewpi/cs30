package io.matthewp.cs30project.dcl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

/**
 * DynamicConfiguration
 *
 * ?
 */
public abstract class DynamicConfiguration {
    @Getter(AccessLevel.PACKAGE) private final File file;
    @Getter(AccessLevel.PACKAGE) private final DynamicLoader2 loader;
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
        this.loader = new DynamicLoader2(file);
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
}
