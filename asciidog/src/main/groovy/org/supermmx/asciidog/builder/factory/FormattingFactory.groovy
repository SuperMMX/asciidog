package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

class FormattingFactory extends InlineContainerFactory {
    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        FormattingNode formattingNode = new FormattingNode()
        
        return formattingNode
    }
}
