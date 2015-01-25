package org.supermmx.asciidog.ast

class Attribute {
    static enum ValueType {
        STRING, BOOLEAN, DECIMAL
    }

    /**
     * Attribute name, read-only
     */
    String name
    /**
     * Attribute value type, read-only
     */
    ValueType type
    /**
     * Attribute value
     */
    Object value

    private void setName(String name) {
        this.name = name
    }

    private void setType(ValueType type) {
        this.type = type
    }

    static Attribute createAttribute(String name, String value) {
        def result = null

        ValueType type = Document.getAttributeType(name)
        if (type == null) {
            type = ValueType.STRING
        }
        switch (type) {
        case ValueType.BOOLEAN:
            result = createBooleanAttribute(name, value)
            break
        case ValueType.BIGDECIMAL:
            result = createDecimalAttribute(name, value)
            break
        default:
            result = createStringAttribute(name, value)
            break
        }
        return result
    }

    static Attribute createStringAttribute(String name, String value) {
        return new Attribute([ name: name, type: ValueType.STRING, value: value ])
    }

    static Attribute createBooleanAttribute(String name, boolean value) {
        return new Attribute([ name: name, type: ValueType.BOOLEAN, value: value ])
    }

    static Attribute createBooleanAttribute(String name, String value) {
        def realValue = value
        if (value == null || value.length() == 0) {
            realValue = Boolean.TRUE
        } else {
            realValue = Boolean.valueOf(value)
        }

        return new Attribute([ name: name, type: ValueType.BOOLEAN, value: realValue ])
    }

    static Attribute createDecimalAttribute(String name, BigDecimal value) {
        return new Attribute([ name: name, type: ValueType.BIGDECIMAL, value: value ])
    }

    static Attribute createDecimalAttribute(String name, String value) {
        return new Attribute([ name: name, type: ValueType.BIGDECIMAL, value: value ])
    }
}
