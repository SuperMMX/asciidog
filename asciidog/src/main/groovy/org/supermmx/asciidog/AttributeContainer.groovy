package org.supermmx.asciidog

import static org.supermmx.asciidog.Attribute.ValueType

import org.supermmx.asciidog.ast.Document

/**
 * The attribute may change in the document, and this may affect
 * the parsing and converting process, the attributes set/update/unset
 * need to be tracked and also the latest values.
 *
 * This AttributeContainer keeps the latest values, and AttributeEntry
 * is used to track the actions in parsing and converting process.
 */
class AttributeContainer {
    static final String UNSET = '!'
    // Default attributes
    static Map<String, Attribute> DEFAULT_ATTRIBUTES = [:]

    // System attributes
    Map<String, Attribute> systemAttributes = [:]
    // Document attributes
    Map<String, Attribute> attributes = [:]

    Attribute setSystemAttribute(String name, String value) {
        setAttribute(name, value, true)
    }

    Attribute setAttribute(String name, String value) {
        setAttribute(name, value, false)
    }

    Attribute setAttribute(String name, String value, boolean isSystem) {
        def unset = false

        // get the name
        if (name[0] == UNSET || name[-1] == UNSET) {
            unset = true
            name = name.replaceAll(UNSET, '')
        }

        if (unset) {
            // remove from the document attributes
            Attribute attr = attributes.remove(name)
            return attr
        }

        def defAttr = getDefaultAttribute(name)
        def defValue = null

        // determine the type
        ValueType type = ValueType.STRING
        if (defAttr != null) {
            type = defAttr.type
            defValue = defValue
        }

        def finalValue = null

        // determine the value
        if (value == null || value.length() == 0) {
            // use default value if null or blank
            finalValue = defValue
        } else {
            // get the value with correct type
            switch (type) {
            case ValueType.BOOLEAN:
                finalValue = Boolean.valueOf(value)
                break
            case ValueType.INTEGER:
                finalValue = Integer.valueOf(value)
                break
            default:
                finalValue = value
                break
            }
        }

        def attr = new Attribute([ name: name,
                                   type: type,
                                   value: finalValue ])

        // put the attribute into correct map
        if (isSystem) {
            systemAttributes[name] = attr
        } else {
            attributes[name] = attr
        }

        return getAttribute(name)
    }

    Attribute getAttribute(String name) {
        // system first
        Attribute attr = systemAttributes[name]

        // then document attribute
        if (attr == null) {
            attr = attributes[name]
        }

        // then default attribute
        if (attr == null) {
            attr = DEFAULT_ATTRIBUTES[name]
        }

        return attr
    }

    /*
    static Attribute createAttribute(String name, String value) {
        def result = null

        ValueType type = getAttributeType(name)
        if (type == null) {
            type = ValueType.STRING
        }

        return createAttribute(name, type, value)
    }

    static Attribute createAttribute(String name, ValueType type, String value) {
        def result = null

        switch (type) {
        case ValueType.BOOLEAN:
            result = createBooleanAttribute(name, value)
            break
        case ValueType.INTEGER:
            result = createIntegerAttribute(name, value)
            break
        default:
            result = createStringAttribute(name, value)
            break
        }
        return result
    }

    static Attribute createStringAttribute(String name, String value) {
        if (value == null || value.length() == 0) {
            def attr = AttributeContainer.getDefaultAttribute(name)
            if (attr != null) {
                value = attr.value
            } else {
                value = ''
            }
        }
        return new Attribute([ name: name, type: ValueType.STRING, value: value ])
    }

    static Attribute createBooleanAttribute(String name, boolean value) {
        return new Attribute([ name: name, type: ValueType.BOOLEAN, value: value ])
    }

    static Attribute createBooleanAttribute(String name, String value) {
        def realValue = null
        if (value == null || value.length() == 0) {
            def attr = AttributeContainer.getDefaultAttribute(name)
            if (attr != null) {
                realValue = attr.value
            } else {
                realValue = Boolean.TRUE
            }
        } else {
            realValue = Boolean.valueOf(value)
        }

        return new Attribute([ name: name, type: ValueType.BOOLEAN, value: realValue ])
    }

    static Attribute createIntegerAttribute(String name, int value) {
        return new Attribute([ name: name, type: ValueType.INTEGER, value: value ])
    }

    static Attribute createIntegerAttribute(String name, String value) {
        def realValue = null
        if (value == null || value.length() == 0) {
            def attr = AttributeContainer.getDefaultAttribute(name)
            if (attr != null) {
                realValue = attr.value
            } else {
                realValue = Integer.valueOf(value)
            }
        } else {
            realValue = Boolean.valueOf(value)
        }

        return new Attribute([ name: name, type: ValueType.DECIMAL, value: value ])
    }
    */

    /**
     * Get the value type of the known attribute with the specified name
     *
     * @param name the attribute name
     *
     * @return the attribute type, ValueType.STRING if the attribute
     *         is not a system attribute
     */
    static Attribute.ValueType getAttributeType(String name) {
        def type = Attribute.ValueType.STRING

        Attribute attr = DEFAULT_ATTRIBUTES[name]
        if (attr != null) {
            type = attr.type
        }

        return type
    }

    static Attribute getDefaultAttribute(String name) {
        return DEFAULT_ATTRIBUTES[name]
    }

    // name, type, default value
    private static def DEFAULT_ATTR_DEFS = [
        [ Document.DOCTYPE, Attribute.ValueType.STRING, Document.DocType.article.toString() ],
        [ Document.TOC, Attribute.ValueType.STRING, 'auto' ],
    ]

    static {
        DEFAULT_ATTR_DEFS.each {
            def name = it[0]
            def type = it[1]
            def value = it[2]
            DEFAULT_ATTRIBUTES[name] = new Attribute([ name: name, type: type, value: value ])
        }
    }
}
