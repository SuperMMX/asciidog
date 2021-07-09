package org.supermmx.asciidog.ast;

import org.supermmx.asciidog.Parser

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * The Link inline node
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class LinkNode extends Inline {
    LinkNode() {
        type = Node.Type.LINK
    }

    /**
     * The target of the link
     */
    String target
}
