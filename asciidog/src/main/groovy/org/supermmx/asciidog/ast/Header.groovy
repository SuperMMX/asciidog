package org.supermmx.asciidog.ast;

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * Document header.  The children blocks are authors, revisions and attributes
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class Header extends Block {
    public Header() {
        type = Node.Type.HEADER
    }
}
