package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Header

class DocumentFactory extends AbstractBlockFactory {
    DocumentFactory() {
        name = 'document'
        childClasses = [
            Header,
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

    @Override
    void doSetChild(FactoryBuilderSupport builder, parent, child) {
        if (child in Header) {
            parent.header = child
        } else if (child in Preamble) {
            parent.preamble = child
        } else {
            super.doSetChild(builder, parent, child)
        }
    }
}
