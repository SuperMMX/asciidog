package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractLeafNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5InlineXrefRenderer extends AbstractLeafNodeRenderer {
    Html5InlineXrefRenderer() {
        nodeType = Node.Type.INLINE_CROSS_REFERENCE
    }

    void doPre(DocumentContext context, Node xrefNode) {
        context.writer.with {
            writeStartElement('a')
            writeAttribute('href', "#${xrefNode.xrefId}")
        }
    }

    void doRender(DocumentContext context, Node xrefNode) {
        context.writer.writeCharacters(context.document.references[(xrefNode.xrefId)].title)
    }

    void doPost(DocumentContext context, Node xrefNode) {
        context.writer.writeEndElement()
    }
}
