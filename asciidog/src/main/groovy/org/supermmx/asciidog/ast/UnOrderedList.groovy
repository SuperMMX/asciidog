package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class UnOrderedList extends AdocList {
    UnOrderedList() {
        type = Node.Type.UNORDERED_LIST
    }
}
