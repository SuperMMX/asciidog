package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class AbstractHtml5FormattingRenderer extends AbstractNodeRenderer {
    protected String tag

    void doPre(DocumentContext context, Node formattingNode) {
        context.writer.writeStartElement(tag)

        // TODO: save the tag in context
    }

    void doPost(DocumentContext context, Node textNode) {
        context.writer.writeEndElement()
    }
}
