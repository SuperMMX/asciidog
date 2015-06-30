package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Section

class DocumentFactory extends AbstractBlockFactory {
    DocumentFactory() {
        childClasses = [
            Section,
        ]
    }

    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        Document document = new Document()
        
        return document
    }
}
