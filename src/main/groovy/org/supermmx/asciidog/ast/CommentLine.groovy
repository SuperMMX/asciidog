package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * The list contains multiple list items with the same level.
 */
@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

class CommentLine extends Block {
    CommentLine() {
        type = Node.Type.COMMENT_LINE
    }
}
