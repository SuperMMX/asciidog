package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class BlockMacro extends Block implements Macro {
    BlockMacro() {
        type = Node.Type.MACRO
    }
}
