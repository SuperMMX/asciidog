package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.TextNode

class TextFactory extends AbstractInlineFactory {
    TextFactory() {
        name = 'text'
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        TextNode textNode = new TextNode(text: value)

        return textNode
    }
}
