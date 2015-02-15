package org.supermmx.asciidog.ast

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@Canonical
@EqualsAndHashCode(callSuper=true)
@ToString(includeSuper=true, includePackage=false, includeNames=true)

class UnOrderedList extends AdocList {
    UnOrderedList() {
        type = Node.Type.UNORDERED_LIST
    }
}
