package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.ast.ListItem

class ListFactory extends AbstractBlockFactory {
    ListFactory() {
        childClasses = [
            ListItem
        ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        AdocList list = null
        if (name == 'ul') {
            list = new UnOrderedList()
        } else if (name == 'ol') {
            list = new OrderedList()
        }
        return list
    }
}
