package org.supermmx.asciidog

import static org.supermmx.asciidog.Attribute.ValueType

import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * The attribute may change in the document, and this may affect
 * the parsing and converting process, the attributes set/update/unset
 * need to be tracked and also the latest values.
 *
 * This AttributeContainer keeps the latest values, and AttributeEntry
 * is used to track the actions in parsing and converting process.
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class AttributeContainer {
    static final String UNSET = '!'
    // Default attributes
    static Map<String, Attribute> DEFAULT_ATTRIBUTES = [:]

    // System attributes
    Map<String, Attribute> systemAttributes = [:]
    // Document attributes
    Map<String, Attribute> attributes = [:]

    Attribute setSystemAttribute(String name, String value) {
        setAttribute(name, null, value, true)
    }

    Attribute setAttribute(String name, String value) {
        setAttribute(name, null, value, false)
    }

    Attribute removeAttribute(String name) {
        removeAttribute(name, false)
    }

    Attribute setSystemAttribute(String name, Attribute.ValueType type, String value) {
        setAttribute(name, type, value, true)
    }

    Attribute removeSystemAttribute(String name) {
        removeAttribute(name, true)
    }

    void removeSystemAttributes() {
        removeAttributes(true)
    }

    Attribute setAttribute(String name, Attribute.ValueType type, String value) {
        setAttribute(name, type, value, false)
    }

    Attribute setAttribute(String name, Attribute.ValueType type, String value, boolean isSystem) {
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
        if (defAttr != null) {
            if (type != null && type != defAttr.type) {
                userLog.warn "Attribute type ${type} is different from the default attribute type ${defAttr.type}. Use the default type."
            }

            type = defAttr.type
            defValue = defValue
        }

        if (type == null) {
            type = Attribute.ValueType.INLINES
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
            case ValueType.STRING:
                finalValue = value
                break
            default:
                // parse the attribute as a list of inline nodes
                def para = new Paragraph()
                def inlines = Parser.parseInlineNodes(para, value)

                inlines = replaceAttributeReferences(inlines)
                type = Attribute.ValueType.INLINES
                finalValue = inlines

                break
            }
        }

        // delete the attribute if the value is null
        if (value == null) {
            systemAttributes.remove(name)
            attributes.remove(name)

            return null
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

    Attribute removeAttribute(String name, boolean isSystem) {
        def attr = null
        if (isSystem) {
            attr = systemAttributes.remove(name)
        } else {
            attr = attributes.remove(name)
        }

        return attr
    }

    void removeAttributes(boolean isSystem) {
        if (isSystem) {
            systemAttributes.clear()
        } else {
            attributes.clear()
        }
    }

    /**
     * Replace the attribute references in the inlines
     */
    public List<Inline> replaceAttributeReferences(List<Inline> inlines) {
        def result = []
        inlines.each { inline ->
            if (inline instanceof InlineContainer) {
                // replace recursively
                inline.inlineNodes = replaceAttributeReferences(inline.inlineNodes)
                result << inline
            } else if (inline instanceof AttributeReferenceNode) {
                // replace the reference
                def attr = getAttribute(inline.name)
                if (attr.type == Attribute.ValueType.INLINES) {
                    // inline value
                    result.addAll(replaceAttributeReferences(attr.value))
                } else {
                    // normal value
                    result << new TextNode(attr.value, 0)
                }
            } else {
                // other inlines
                result << inline
            }
        }

        return result
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

        [ Document.OUTPUT_BASE, Attribute.ValueType.STRING, '' ],
        [ Document.OUTPUT_CHUNKED, Attribute.ValueType.BOOLEAN, false ],
        [ Document.OUTPUT_STREAM, Attribute.ValueType.BOOLEAN, false ],
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
