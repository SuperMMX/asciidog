package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.Utils

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Node factory that only accepts define classes as the children
 */
@Slf4j
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
        } else {
            log.warn('Child {} with class {} not accepted by parent {}',
                     builder.getProperty(builder.CURRENT_NAME),
                     child.getClass(),
                     builder.getProperty(builder.PARENT_NAME))
        }
    }

    @Override
    void setParent(FactoryBuilderSupport builder, parent, child) {
        child.parent = parent
        child.document = parent.document

        // update references when the id is not null
        if (child.id != null) {
            if (child.document != null) {
                child.document.references[(child.id)] = child
            }
        }
    }

    @Override
    boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        // generate default ids
        if (node in Section) {
            if (attributes['id'] == null) {
                // Update ID
                Utils.generateId(node)
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
