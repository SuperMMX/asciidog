package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Preamble

class PreambleFactory extends AbstractBlockFactory {
    PreambleFactory() {
        name = 'preamble'

        childClasses = BLOCKS_CLASSES
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Preamble preamble = new Preamble()

        return preamble
    }
}
