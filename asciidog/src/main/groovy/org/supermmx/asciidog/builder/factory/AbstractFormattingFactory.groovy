package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

abstract class AbstractFormattingFactory extends InlineContainerFactory {
    FormattingNode.FormattingType formattingType

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        FormattingNode formattingNode = new FormattingNode(formattingType: formattingType)
        
        return formattingNode
    }
}
