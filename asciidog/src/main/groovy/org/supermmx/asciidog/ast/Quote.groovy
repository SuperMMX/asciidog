package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Quote extends StyledBlock {
    Quote() {
        type = Node.Type.QUOTE_BLOCK
    }
}
