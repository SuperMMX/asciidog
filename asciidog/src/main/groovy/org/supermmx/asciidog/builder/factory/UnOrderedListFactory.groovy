package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.ast.ListItem

class UnOrderedListFactory extends AbstractListFactory {
    UnOrderedListFactory() {
        name = 'ul'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        UnOrderedList list = new UnOrderedList()

        return list
    }
}
