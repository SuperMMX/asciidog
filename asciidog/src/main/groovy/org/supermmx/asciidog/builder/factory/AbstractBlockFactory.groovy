package org.supermmx.asciidog.builder.factory

abstract class AbstractBlockFactory extends AbstractFactory {
    def childClasses = []

    boolean isLeaf() {
        false
    }

    void setChild(FactoryBuilderSupport builder, parent, child) {
        if (accept(child)) {
            parent << child
        }
    }

    boolean accept(def child) {
        return childClasses.contains(child.getClass())
    }
}
