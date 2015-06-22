package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5ParagraphRenderer extends AbstractNodeRenderer {
    Html5ParagraphRenderer() {
        nodeType = Node.Type.PARAGRAPH
    }

    void doPre(DocumentContext context, Node paragraph) {
        context.writer.writeStartElement('p')
    }

    void doPost(DocumentContext context, Node paragraph) {
        context.writer.writeEndElement()
    }
}
