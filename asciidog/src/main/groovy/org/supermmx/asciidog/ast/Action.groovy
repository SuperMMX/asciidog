package org.supermmx.asciidog.ast

/**
 * Abstract action node
 */
abstract class Action extends Block {
    Action() {
        type = Node.Type.ACTION
    }
}
