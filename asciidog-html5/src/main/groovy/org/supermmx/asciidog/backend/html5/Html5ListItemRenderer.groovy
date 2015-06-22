package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5ListItemRenderer extends AbstractNodeRenderer {
    Html5ListItemRenderer() {
        nodeType = Node.Type.LIST_ITEM
    }

    void doPre(DocumentContext context, Node item) {
        context.writer.writeStartElement('li')
    }

    void doPost(DocumentContext context, Node item) {
        context.writer.writeEndElement()
    }
}
