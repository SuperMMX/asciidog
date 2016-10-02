package org.supermmx.asciidog.ast

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode(callSuper=true)
@TupleConstructor
class OrderedList extends AdocList {
    OrderedList() {
        type = Node.Type.ORDERED_LIST
    }
}
