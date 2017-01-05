package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor

class AttributeEntry extends Action {
    /**
     * Attribute name, read-only
     */
    String name
    /**
     * Attribute value
     */
    String value

    AttributeEntry() {
        type = Node.Type.DEFINE_ATTRIBUTE
    }

    private void setName(String name) {
        this.name = name
    }
}
