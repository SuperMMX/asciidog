package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.CrossReferenceNode

class CrossReferenceFactory extends InlineContainerFactory {
    CrossReferenceFactory() {
        name = 'xref'
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        CrossReferenceNode xrefNode = new CrossReferenceNode(xrefId: value)

        return xrefNode
    }
}
