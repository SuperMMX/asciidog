package org.supermmx.asciidog.ast

/**
 * Blank node that only contains block header
 */
class Blank extends Block {
    Blank() {
        type = Node.Type.BLANK
    }
}
