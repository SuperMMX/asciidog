package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Section extends Block {
    int level

    Section() {
        type = Node.Type.SECTION
    }
}
