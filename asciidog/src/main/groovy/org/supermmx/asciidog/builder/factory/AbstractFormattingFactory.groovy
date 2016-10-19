package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

abstract class AbstractFormattingFactory extends InlineContainerFactory {
    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        FormattingNode formattingNode = createFormattingNode()

        return formattingNode
    }

    abstract FormattingNode createFormattingNode()
}
