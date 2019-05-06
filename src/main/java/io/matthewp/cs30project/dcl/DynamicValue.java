package io.matthewp.cs30project.dcl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * DynamicValue
 *
 * ?
 */
public final class DynamicValue {
    @Getter(AccessLevel.PRIVATE) private final Object value;
    @Getter(AccessLevel.PUBLIC) private final ValueType type;
    @Getter(AccessLevel.PROTECTED) private final int lineNumber;

    /**
     * DynamicValue
     *
     * Creates a new {@link DynamicValue} object.
     *
     * @param value Value
     * @param type Value type
     */
    DynamicValue(@NonNull final Object value, @NonNull final ValueType type) {
        this(value, type, 0);
    }

    DynamicValue(@NonNull final Object value, @NonNull final ValueType type, final int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
    }

    public String asString() {
        if(this.type != ValueType.STRING)
            return null;

        return (String) this.getValue();
    }

    public Boolean asBoolean() {
        if(this.type != ValueType.BOOLEAN)
            return null;

        return (Boolean) this.getValue();
    }

    public Integer asInteger() {
        if(this.type != ValueType.INTEGER)
            return null;

        return (Integer) this.getValue();
    }

    public Double asDouble() {
        if(this.type != ValueType.DOUBLE)
            return null;

        return (Double) this.getValue();
    }

    public List<String> asStringList() {
        if(this.type != ValueType.STRING_LIST)
            return null;

        return (List<String>) this.getValue();
    }

    public List<Integer> asIntegerList() {
        if(this.type != ValueType.INTEGER_LIST)
            return null;

        return (List<Integer>) this.getValue();
    }

    public DynamicSection asSection() {
        if(this.type != ValueType.SECTION)
            return null;

        return (DynamicSection) this.getValue();
    }

    public String value() {
        if(this.getType() == ValueType.STRING)
            return this.asString();

        if(this.getType() == ValueType.BOOLEAN)
            return String.valueOf(this.asBoolean());

        if(this.getType() == ValueType.INTEGER)
            return String.valueOf(this.asInteger());

        if(this.getType() == ValueType.DOUBLE)
            return this.asDouble().toString();

        if(this.getType() == ValueType.STRING_LIST)
            return String.valueOf(this.asStringList());

        if(this.getType() == ValueType.INTEGER_LIST)
            return String.valueOf(this.asIntegerList());

        if(this.getType() == ValueType.SECTION)
            return this.asSection().getKey();

        return null;
    }

    @Override
    public String toString() {
        return this.value();
    }

    public enum ValueType {
        STRING,
        BOOLEAN,
        INTEGER,
        DOUBLE,
        STRING_LIST,
        INTEGER_LIST,
        SECTION
    }
}
