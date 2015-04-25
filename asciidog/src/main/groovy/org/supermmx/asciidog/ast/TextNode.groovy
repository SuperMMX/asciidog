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
class TextNode extends Inline {
    String text

    TextNode() {
        type = Node.Type.INLINE_TEXT
        info.escaped = false
        info.constrained = false
    }

    TextNode(String text, int startIndex) {
        // why need to call this in groovy??
        this()

        this.text = text
        this.info.with {
            start = startIndex
            end = startIndex + text.length()
            contentStart = start
            contentEnd = end
        }
    }
}
