package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

class StrongFormattingFactory extends AbstractFormattingFactory {
    StrongFormattingFactory() {
        name = 'strong'
        formattingType = FormattingNode.FormattingType.STRONG
    }
}
