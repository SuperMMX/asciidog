package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.TextNode

class TextFactory extends NodeFactory {
    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        TextNode textNode = new TextNode(text: value)

        return textNode
    }
}
