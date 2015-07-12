package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Section

abstract class AbstractBlockFactory extends AbstractNodeFactory {
    def BLOCKS_CLASSES = [
        Paragraph,
        AdocList,
    ]

    def SECTION_CLASSES = [ Section ] + BLOCKS_CLASSES
}
