package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.AttributeEntry

class HeaderFactory extends AbstractBlockFactory {
    HeaderFactory() {
        name = 'header'
        childClasses = [
            AttributeEntry
        ]
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Header header = new Header()

        return header
    }
}
