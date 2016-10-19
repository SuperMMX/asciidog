package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.EmphasisFormattingNode

class EmphasisFormattingFactory extends AbstractFormattingFactory {
    EmphasisFormattingFactory() {
        name = 'em'
    }

    FormattingNode createFormattingNode() {
        return new EmphasisFormattingNode()
    }
}
