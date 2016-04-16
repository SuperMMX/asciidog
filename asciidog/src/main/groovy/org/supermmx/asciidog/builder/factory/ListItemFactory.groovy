package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section

class ListItemFactory extends AbstractBlockFactory {
    ListItemFactory() {
        name = 'item'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        ListItem item = new ListItem()
        return item
    }

    @Override
    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return !(child.getClass() in Section)
    }
}
