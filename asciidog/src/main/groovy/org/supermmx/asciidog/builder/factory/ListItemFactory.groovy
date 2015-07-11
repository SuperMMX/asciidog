package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Paragraph

class ListItemFactory extends AbstractBlockFactory {
    ListItemFactory() {
        childClasses = [
            Paragraph,
            AdocList
        ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        ListItem item = new ListItem()
        return item
    }
}
