package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.Section

class DocumentFactory extends AbstractBlockFactory {
    DocumentFactory() {
        childClasses = [
            Preamble,
            Section,
        ]
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Document document = new Document()
        document.document = document
        
        return document
    }
}
