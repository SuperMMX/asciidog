package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

class EmphasisFormattingFactory extends AbstractFormattingFactory {
    EmphasisFormattingFactory() {
        name = 'em'
        formattingType = FormattingNode.FormattingType.EMPHASIS
    }
}
