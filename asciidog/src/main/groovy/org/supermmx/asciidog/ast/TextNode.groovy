package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * A inline node with simple text.
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class TextNode extends Inline {
    String text

    TextNode() {
        type = Node.Type.TEXT
        escaped = false
    }

    TextNode(String text) {
        this()

        this.text = text
    }

    @Override
    void asText(StringBuilder buf) {
        buf.append(text)
    }
}
