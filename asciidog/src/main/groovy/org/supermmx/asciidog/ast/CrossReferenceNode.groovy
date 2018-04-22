package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * The cross reference inline node.
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class CrossReferenceNode extends Inline {
    // id
    String xrefId

    CrossReferenceNode() {
        type = Node.Type.CROSS_REFERENCE
    }
}
