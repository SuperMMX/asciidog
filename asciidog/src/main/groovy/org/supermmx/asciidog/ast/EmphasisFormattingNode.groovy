package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * An inline node with formatted text
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class EmphasisFormattingNode extends FormattingNode {
    EmphasisFormattingNode() {
        type = Node.Type.EMPHASIS
    }
}
