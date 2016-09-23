package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.AttributeEntry

class HeaderFactory extends AbstractBlockFactory {
    HeaderFactory() {
        name = 'header'

        childClasses = [
            Authors,
            // TODO: revision
            AttributeEntry
        ]
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Header header = new Header(title: value)

        return header
    }
}
