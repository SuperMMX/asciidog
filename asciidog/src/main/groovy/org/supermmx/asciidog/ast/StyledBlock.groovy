package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
abstract class StyledBlock extends Block {
    /**
     * Whether delimiter is explicitly specified
     */
    boolean hasDelimiter = true
    /**
     * Whether the delimiter is open block delimiter or not
     */
    boolean isOpenBlock = false

    StyledBlock() {
        type = Node.Type.STYLED_BLOCK
    }
}
