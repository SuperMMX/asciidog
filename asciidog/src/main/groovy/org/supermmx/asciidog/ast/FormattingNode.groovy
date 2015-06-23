package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
/**
 * An inline node with formatted text
 */
class FormattingNode extends InlineContainerNode {
    static enum FormattingType {
        STRONG,
        EMPHASIS,
        MONOSPACED,
        SUPERSCRIPT,
        SUBSCRIPT
    }

    FormattingType formattingType

    FormattingNode() {
        type = Node.Type.FORMATTING
    }
}
