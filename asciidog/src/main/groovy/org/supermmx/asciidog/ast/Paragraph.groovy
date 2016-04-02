package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * A paragraph block that only contains inline nodes
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)
@Slf4j
class Paragraph extends Block implements InlineContainer  {
    List<Inline> inlineNodes = []

    Paragraph() {
        type = Node.Type.PARAGRAPH
    }

    Paragraph leftShift(Node node) {
        // only allow action block in a paragraph
        if (node in Action) {
            blocks << node
        } else if (node in Inline) {
            inlineNodes << node
        }

        return this
    }
}
