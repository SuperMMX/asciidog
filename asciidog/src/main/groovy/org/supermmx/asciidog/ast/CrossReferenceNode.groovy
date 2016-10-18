package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * A inline node with simple text.
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class CrossReferenceNode extends Inline {
    String xrefId
    // id, text (title or caption), number, page
    String xrefText

    CrossReferenceNode() {
        type = Node.Type.CROSS_REFERENCE
    }
}
