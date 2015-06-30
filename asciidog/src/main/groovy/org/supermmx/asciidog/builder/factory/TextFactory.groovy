package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.TextNode

class TextFactory extends AbstractFactory {
    boolean isLeaf() {
        true
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        TextNode textNode = new TextNode()
        textNode.text = value
        
        return textNode
    }
}
