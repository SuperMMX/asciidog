package org.supermmx.asciidog.ast

import org.supermmx.asciidog.ast.Node

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * An inline container that contains multiple inline nodes.
 */
trait InlineContainer {
    abstract List<Node> getChildren()

    InlineContainer leftShift(Inline inline) {
        children << inline

        return this
    }
}
