package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.OpenBlock

class OpenBlockFactory extends AbstractStyledBlockFactory {
    OpenBlockFactory() {
        name = 'openBlock'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        OpenBlock openBlock = new OpenBlock()

        return openBlock
    }
}
