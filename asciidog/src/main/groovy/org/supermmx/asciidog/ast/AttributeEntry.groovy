package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * The attribute definition block in the document header.
 * The value is parsed as a list of inline nodes saved in children
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class AttributeEntry extends Action implements InlineContainer {
    /**
     * Attribute name, read-only
     */
    String name

    AttributeEntry() {
        type = Node.Type.DEFINE_ATTRIBUTE
    }

    private void setName(String name) {
        this.name = name
    }
}
