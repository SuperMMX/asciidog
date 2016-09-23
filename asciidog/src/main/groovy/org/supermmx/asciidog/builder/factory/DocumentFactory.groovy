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
        boolean callSuper = true
        if (child in Header) {
            if (parent.children.size() > 0) {
                callSuper = false
                throw new Exception('"header" should be the first child in "document"')
            }
        } else if (child in Preamble) {
            if (parent.children.size() == 1) {
                if (!(parent.blocks[0] in Header)) {
                    callSuper = false
                    throw new Exception('"preamble" should be after "header" and before "section"')
                }
            } else if (parent.children.size() > 1) {
                callSuper = false
                throw new Exception('"preamble" should be the first or second child in "document"')
            }
        }

        if (callSuper) {
            super.doSetChild(builder, parent, child)
        }
    }
}
