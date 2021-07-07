package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.MarkFormattingNode

class MarkFormattingFactory extends AbstractFormattingFactory {
    MarkFormattingFactory() {
        name = 'mark'
    }

    FormattingNode createFormattingNode() {
        return new MarkFormattingNode()
    }
}
