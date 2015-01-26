package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includeFields=true)
class AttributeEntry extends Block {
    /**
     * Attribute name, read-only
     */
    String name
    /**
     * Attribute value
     */
    String value

    private void setName(String name) {
        this.name = name
    }
}
