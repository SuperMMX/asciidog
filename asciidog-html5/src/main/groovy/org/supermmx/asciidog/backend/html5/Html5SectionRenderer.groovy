package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5SectionRenderer extends AbstractNodeRenderer {
    Html5SectionRenderer() {
        nodeType = Node.Type.SECTION
    }

    void doPre(DocumentContext context, Node section) {
        context.writer.with {
            writeStartElement('h2')
            writeAttribute('id', section.id)
            writeCharacters(section.title)
            writeEndElement()
        }
    }

    void doPost(DocumentContext context, Node section) {
    }
}
