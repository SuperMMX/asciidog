package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Action
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.StyledBlock

abstract class AbstractBlockFactory extends AbstractNodeFactory {
    AbstractBlockFactory() {
        childClasses << Action
        childClasses << Block
    }

    @Override
    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return super.accept(builder, parent, child) && !(child.getClass() in Section)
    }
}
