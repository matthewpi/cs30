package io.matthewp.cs30project.dcl;

import com.sun.javaws.exceptions.InvalidArgumentException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;

/**
 * DynamicValue
 *
 * ?
 */
public final class DynamicValue {
    @Getter(AccessLevel.PRIVATE) private Object value;
    @Getter(AccessLevel.PUBLIC) private ValueType type;
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
    DynamicValue(@NonNull final Object value, @NonNull final ValueType type) {
        this(value, type, 0);
    }

    DynamicValue(@NonNull final Object value, @NonNull final ValueType type, final int lineNumber) {
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
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

    public DynamicSection asSection() {
        if(this.getType() != ValueType.SECTION)
            return null;

        return (DynamicSection) this.getValue();
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

        if(this.getType() == ValueType.SECTION) {
            return this.asSection().getKey();
        }

        return null;
    }
    
    public void set(@NonNull final Object value) {
        // We should probably update the type of this value.
        if(value instanceof String) {
            this.type = ValueType.STRING;
        } else if(value instanceof Boolean) {
            this.type = ValueType.BOOLEAN;
        } else if(value instanceof Integer) {
            this.type = ValueType.INTEGER;
        } else if(value instanceof Double) {
            this.type = ValueType.DOUBLE;
        } else if(value instanceof DynamicSection) {
            this.type = ValueType.SECTION;
        } else {
            throw new IllegalArgumentException("Invalid object type passed to DynamicValue#set().");
        }

        // TODO: Implement lists.

        this.value = value;
        this.modified = true;
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
