package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Verse extends StyledBlock {
    Verse() {
        type = Node.Type.VERSE_BLOCK
    }
}
