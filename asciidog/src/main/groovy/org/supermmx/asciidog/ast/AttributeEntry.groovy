package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

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
