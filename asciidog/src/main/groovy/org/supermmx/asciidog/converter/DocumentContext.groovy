package org.supermmx.asciidog.converter

import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.backend.Backend

class DocumentContext {
    AttributeContainer attrContainer = new AttributeContainer()

    Document document
    Backend backend

    private def stack = []
    private def properties = [:]

    def propertyMissing(String name, value) {
        properties[(name)] = value
    }

    def propertyMissing(String name) {
        return properties[(name)]
    }

    /**
     * Push current context and keep a copy
     */
    void push() {
        def newProperties = [:]
        newProperties << properties

        stack.push(properties)

        properties = newProperties
    }

    /**
     * Pop the previous context to replace current one
     */
    void pop() {
        if (stack.size() > 0) {
            properties = stack.pop()
        }
    }

    def remove(String name) {
        return properties.remove(name)
    }
}
