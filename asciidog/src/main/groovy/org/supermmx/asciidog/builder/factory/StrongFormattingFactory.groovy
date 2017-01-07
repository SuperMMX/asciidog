package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.StrongFormattingNode

class StrongFormattingFactory extends AbstractFormattingFactory {
    StrongFormattingFactory() {
        name = 'strong'
    }

    FormattingNode createFormattingNode() {
        return new StrongFormattingNode()
    }
}
