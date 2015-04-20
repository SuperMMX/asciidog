package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * A paragraph block that only contains inline nodes
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
class Paragraph extends Block implements InlineContainer  {
    List<Inline> inlineNodes = []
    InlineInfo info = new InlineInfo()

    Paragraph() {
        type = Node.Type.PARAGRAPH
    }

    InlineContainer leftShift(Inline inline) {
        inlineNodes << inline

        return this
    }
}
