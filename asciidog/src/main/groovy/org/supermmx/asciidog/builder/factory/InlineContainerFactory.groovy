package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer

abstract class InlineContainerFactory extends NodeFactory {
    InlineContainerFactory() {
        childClasses = [
            Inline,
            InlineContainer
        ]
    }
}
