package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Preamble extends Block {
    Preamble() {
        type = Node.Type.PREAMBLE
    }
}
