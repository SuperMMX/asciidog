package org.supermmx.asciidog.builder.factory

/**
 * Node factory that only accepts define classes as the children
 */
abstract class NodeFactory extends AbstractFactory {
    def childClasses = []

    boolean accept(def child) {
        return childClasses.contains(child.getClass())
    }
}
