package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * A list item contains multiple blocks, which can be
 * grouped together in the list item with a list continuation.
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class ListItem extends Block {
    ListItem() {
        type = Node.Type.LIST_ITEM
    }
}
