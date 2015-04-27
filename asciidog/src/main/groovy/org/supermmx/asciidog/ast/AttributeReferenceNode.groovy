package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
/**
 * A inline node for attribute reference.
 */
class AttributeReferenceNode extends Inline {
    String name

    AttributeReferenceNode() {
        type = Node.Type.INLINE_ATTRIBUTE_REFERENCE
    }
}
