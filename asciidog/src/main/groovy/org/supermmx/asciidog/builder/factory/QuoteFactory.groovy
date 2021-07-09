package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Quote

class QuoteFactory extends AbstractStyledBlockFactory {
    QuoteFactory() {
        name = 'quote'
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Quote quote = new Quote()

        return quote
    }
}
