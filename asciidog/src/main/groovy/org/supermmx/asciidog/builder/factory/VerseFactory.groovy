package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Verse
import org.supermmx.asciidog.ast.TextNode

class VerseFactory extends AbstractStyledBlockFactory {
    VerseFactory() {
        name = 'verse'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Verse verse = new Verse()

        return verse
    }

    @Override
    boolean accept(FactoryBuilderSupport builder, parent, child) {
        return child in TextNode
    }
}
