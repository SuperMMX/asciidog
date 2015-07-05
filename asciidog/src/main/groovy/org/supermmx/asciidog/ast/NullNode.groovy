package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
/**
 * An inline node acting as a placeholder
 */
class NullNode extends Inline {
    NullNode() {
        type = Node.Type.NULL
    }
}
