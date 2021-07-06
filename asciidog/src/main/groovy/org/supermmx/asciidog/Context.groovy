package org.supermmx.asciidog

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
trait Context {
    public boolean copyLast = false

    def stack = []

    def properties = [:]

    def permProperties = [:]

    def propertyMissing(String name, value) {
        if (value == null) {
            properties.remove(name)
        } else {
            properties[name] = value
        }
    }

    def propertyMissing(String name) {
        return properties[name]
    }

    /*
     * Push current context and keep a shallow copy
     */
    void push() {
        def newProperties = [:]

        if (copyLast) {
            newProperties << properties
        }

        stack.push(properties)

        properties = newProperties
    }

    /**
     * Pop the previous context to replace current one
     */
    boolean pop() {
        def pop = (stack.size() > 0)
        if (pop) {
            properties = stack.pop()
        }

        return pop
    }
}
