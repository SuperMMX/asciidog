package org.supermmx.asciidog.builder.factory

/**
 * Node factory that only accepts define classes as the children
 */
abstract class NodeFactory extends AbstractFactory {
    def childClasses = []

    @Override
    boolean isLeaf() {
        false
    }

    @Override
    void setChild(FactoryBuilderSupport builder, parent, child) {
        if (accept(builder, parent, child)) {
            parent << child
        }
    }

    @Override
    void setParent(FactoryBuilderSupport builder, parent, child) {
        child.parent = parent
        child.document = parent.document
    }

    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return childClasses.find { childClass ->
            child in childClass
        }
    }
}
