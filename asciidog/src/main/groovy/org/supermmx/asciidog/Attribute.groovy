package org.supermmx.asciidog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import groovy.util.logging.Slf4j

/**
 * A AsciiDoc document attribute, which can be set via system/api,
 * or in the document.  There are also default or built-in attributes.
 * If an attribute is not set, the value from the default attribute
 * will be used.
 */
@EqualsAndHashCode(includeFields=true, excludes=["valueString"])
@ToString(includeSuper=false, includePackage=false, includeNames=true)
@Slf4j
class Attribute {
    static enum ValueType {
        STRING, BOOLEAN, INTEGER, INLINES
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
    /**
     * The raw string value
     */
    String valueString

    private void setName(String name) {
        this.name = name
    }

    private void setType(ValueType type) {
        this.type = type
    }

    /**
     * Get the value as string
     */
    public String getValueString() {
        if (valueString != null) {
            return valueString
        }

        if (type == ValueType.INLINES) {
            // convert the inline nodes to string
            def buf = new StringBuilder()
            value?.each { it.asText(buf) }
            valueString = buf.toString()
        } else {
            valueString = value?.toString()
        }

        return valueString
    }
}
