package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.ast.ListItem

class OrderedListFactory extends AbstractListFactory {
    OrderedListFactory() {
        name = 'ol'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        OrderedList list = new OrderedList()

        return list
    }
}
