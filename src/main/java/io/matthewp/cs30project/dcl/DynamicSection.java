package io.matthewp.cs30project.dcl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DynamicSection
 *
 * This class is used to store a configuration section and it's {@link DynamicValue}s.
 */
public final class DynamicSection {
    @Getter private final String key;
    @Getter private final Map<String, DynamicValue> values;
    @Getter @Setter(AccessLevel.PROTECTED) private int lineNumber;
    @Getter @Setter(AccessLevel.PROTECTED) private int endLineNumber;
    @Getter @Setter(AccessLevel.PROTECTED) public boolean modified;

    public DynamicSection(@NonNull final String key) {
        this(key, 0);
    }

    /**
     * DynamicSection(String)
     *
     * Creates a new {@link DynamicSection} object.
     *
     * @param key Key
     */
    public DynamicSection(@NonNull final String key, final int lineNumber) {
        this.key = key;
        this.values = new LinkedHashMap<>();
        this.lineNumber = lineNumber;
        this.endLineNumber = 0;
        this.modified = false;
    }

    /**
     * get(String)
     *
     * Gets a {@link DynamicValue} from the `this.values` map.
     *
     * @param key Key
     * @return Found value or null.
     */
    public DynamicValue get(@NonNull final String key) {
        if(key.length() == 0) {
            return null;
        }

        return this.getValues().get(key);
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asString();
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asBoolean();
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asInteger();
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asDouble();
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asStringList();
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
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asIntegerList();
    }

    /**
     * addValue(String, DynamicValue);
     *
     * Add a {@link DynamicValue} to the section.
     *
     * @param key Key
     * @param value Value
     */
    public void addValue(@NonNull final String key, @NonNull final DynamicValue value) {
        this.getValues().put(key, value);
    }
}
