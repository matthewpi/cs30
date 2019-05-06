package io.matthewp.cs30project.dcl;

import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DynamicSection
 *
 * ?
 */
public final class DynamicSection {
    @Getter private final String key;
    @Getter private final Map<String, DynamicValue> values;

    /**
     * DynamicSection(String)
     *
     * Creates a new {@link DynamicSection} object.
     *
     * @param key Key
     */
    public DynamicSection(@NonNull final String key) {
        this.key = key;
        this.values = new LinkedHashMap<>();
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
     * getSection(String)
     *
     * Gets a {@link DynamicSection}.
     *
     * @param key Key
     * @return Found section or null.
     */
    public DynamicSection getSection(@NonNull final String key) {
        final DynamicValue value = this.get(key);

        if(value == null) {
            return null;
        }

        return value.asSection();
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
