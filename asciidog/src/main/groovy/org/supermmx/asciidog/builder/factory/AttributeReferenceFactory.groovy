package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AttributeReferenceNode

class AttributeReferenceFactory extends NodeFactory {
    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        AttributeReferenceNode aref = new AttributeReferenceNode(name: value)

        return aref
    }
}
