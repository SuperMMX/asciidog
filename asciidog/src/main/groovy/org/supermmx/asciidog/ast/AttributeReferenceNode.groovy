package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
/**
 * The attribute reference inline node.
 */
// TODO: split this node by action
class AttributeReferenceNode extends Inline {
    static enum Action {
        REFERENCE, SET, COUNTER, COUNTER2
    }

    Action action = Action.REFERENCE
    String name

    AttributeReferenceNode() {
        type = Node.Type.ATTRIBUTE_REFERENCE
    }
}
