package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * An inline node with formatted text
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class FormattingNode extends Inline implements InlineContainer {
    static enum FormattingType {
        STRONG,
        EMPHASIS,
    }

    FormattingType formattingType

    FormattingNode() {
        type = Node.Type.FORMATTING
    }
}
