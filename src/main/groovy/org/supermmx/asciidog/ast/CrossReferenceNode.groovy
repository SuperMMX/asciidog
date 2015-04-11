package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
/**
 * A inline node with simple text.
 */
class CrossReferenceNode extends Inline {
    String xrefId
    String xrefText

    CrossReferenceNode() {
        type = Node.Type.INLINE_CROSS_REFERENCE
        info.escaped = false
        info.constrained = false
    }
}
