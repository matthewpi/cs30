package io.matthewp.cs30project.dcl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * DynamicValue
 *
 * This class is used to store a configuration value.
 */
public final class DynamicValue {
    @Getter(AccessLevel.PRIVATE) private Object value;
    @Getter private ValueType type;
    @Getter(AccessLevel.PROTECTED) @Setter private int lineNumber;
    @Getter @Setter(AccessLevel.PROTECTED) public boolean modified;

    /**
     * DynamicValue
     *
     * Creates a new {@link DynamicValue} object.
     *
     * @param value Value
     * @param type Value type
     */
    DynamicValue(@NonNull final Object value, final ValueType type) {
        this(value, type, 0);
    }

    DynamicValue(@NonNull final Object value, final ValueType type, final int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;

        if(this.getType() == null) {
            this.updateType(value);
        }
    }

    public String asString() {
        if(this.getType() != ValueType.STRING)
            return null;

        return (String) this.getValue();
    }

    public Boolean asBoolean() {
        if(this.getType() != ValueType.BOOLEAN)
            return null;

        return (Boolean) this.getValue();
    }

    public Integer asInteger() {
        if(this.getType() != ValueType.INTEGER)
            return null;

        return (Integer) this.getValue();
    }

    public Double asDouble() {
        if(this.getType() != ValueType.DOUBLE)
            return null;

        return (Double) this.getValue();
    }

    public List<String> asStringList() {
        if(this.getType() != ValueType.STRING_LIST)
            return null;

        return (List<String>) this.getValue();
    }

    public List<Integer> asIntegerList() {
        if(this.getType() != ValueType.INTEGER_LIST)
            return null;

        return (List<Integer>) this.getValue();
    }

    public String value() {
        if(this.getType() == ValueType.STRING) {
            return this.asString();
        }

        if(this.getType() == ValueType.BOOLEAN) {
            return String.valueOf(this.asBoolean());
        }

        if(this.getType() == ValueType.INTEGER) {
            return String.valueOf(this.asInteger());
        }

        if(this.getType() == ValueType.DOUBLE) {
            return String.valueOf(this.asDouble());
        }
        
        if(this.getType() == ValueType.STRING_LIST) {
            return String.valueOf(this.asStringList());
        }

        if(this.getType() == ValueType.INTEGER_LIST) {
            return String.valueOf(this.asIntegerList());
        }

        return null;
    }
    
    public void set(@NonNull final Object value) {
        this.updateType(value);

        this.value = value;
        this.modified = true;
    }

    private void updateType(@NonNull final Object value) {
        if(value instanceof String) {
            this.type = ValueType.STRING;
        } else if(value instanceof Boolean) {
            this.type = ValueType.BOOLEAN;
        } else if(value instanceof Integer) {
            this.type = ValueType.INTEGER;
        } else if(value instanceof Double) {
            this.type = ValueType.DOUBLE;
        } else {
            throw new IllegalArgumentException("Invalid object type passed to DynamicValue#updateType().");
        }

        // TODO: Implement lists.
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
        INTEGER_LIST
    }
}
