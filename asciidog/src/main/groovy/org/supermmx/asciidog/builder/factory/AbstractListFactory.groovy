package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.ListItem

abstract class AbstractListFactory extends AbstractBlockFactory {
    AbstractListFactory() {
        childClasses << ListItem
    }
}
