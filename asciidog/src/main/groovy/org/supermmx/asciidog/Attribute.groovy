package org.supermmx.asciidog

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * A AsciiDoc document attribute, which can be set via system/api,
 * or in the document.  There are also default or built-in attributes.
 * If an attribute is not set, the value from the default attribute
 * will be used.
 */
@EqualsAndHashCode(includeFields=true)
@ToString(includeSuper=false, includePackage=false, includeNames=true)
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
}
