package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.Parser

/**
 * Node factory that only accepts define classes as the children
 */
abstract class AbstractNodeFactory extends AbstractFactory {
    /**
     * The node names that this factory can handle
     */
    String name
    /**
     * The classes for accpeted child nodes
     */
    def childClasses = []

    @Override
    boolean isLeaf() {
        childClasses.size() == 0
    }

    @Override
    void setChild(FactoryBuilderSupport builder, parent, child) {
        if (accept(builder, parent, child)) {
            doSetChild(builder, parent, child)
        }
    }

    @Override
    void setParent(FactoryBuilderSupport builder, parent, child) {
        child.parent = parent
        child.document = parent.document

        // update references when the id is not null
        if (child.id != null) {
            child.document.references[(child.id)] = child
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        // generate default ids
        if (node in Section) {
            if (attributes['id'] == null) {
                // Update ID
                Parser.generateId(node)
            }
        }

        return true
    }

    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return childClasses.find { childClass ->
            child in childClass
        }
    }

    void doSetChild(FactoryBuilderSupport builder, parent, child) {
        parent << child
    }
}
