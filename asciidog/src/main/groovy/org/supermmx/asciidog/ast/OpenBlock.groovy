package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class OpenBlock extends StyledBlock {
    OpenBlock() {
        type = Node.Type.OPEN_BLOCK

        hasDelimiter = true
        isOpenBlock = true
    }
}
