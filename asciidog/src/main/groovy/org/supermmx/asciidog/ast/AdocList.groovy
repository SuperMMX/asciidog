package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

/**
 * The list contains multiple list items with the same level.
 */
@EqualsAndHashCode(callSuper=true)
@TupleConstructor
abstract class AdocList extends Block {
    /**
     * the leading spaces
     */
    String lead
    String marker
    int markerLevel
    int level

    AdocList() {
        type = Node.Type.LIST
    }
}
