package org.supermmx.asciidog.ast

class Section extends Block {
    int level

    Section() {
        type = Node.Type.SECTION
    }
}
