package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Verse

class VerseFactory extends AbstractStyledBlockFactory {
    VerseFactory() {
        name = 'verse'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Verse verse = new Verse()

        return verse
    }
}
