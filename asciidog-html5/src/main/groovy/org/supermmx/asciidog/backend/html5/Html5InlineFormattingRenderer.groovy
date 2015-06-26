package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5InlineFormattingRenderer extends AbstractNodeRenderer {
    Html5InlineFormattingRenderer() {
        nodeType = Node.Type.FORMATTING
    }

    void doPre(DocumentContext context, Node formattingNode) {
        def tag = ''

        switch (formattingNode.formattingType) {
        case FormattingNode.FormattingType.STRONG:
            tag = 'strong'
            break
        case FormattingNode.FormattingType.EMPHASIS:
            tag = 'em'
            break
        case FormattingNode.FormattingType.MONOSPACED:
            tag = 'span'
            break
        case FormattingNode.FormattingType.SUPERSCRIPT:
            tag = 'sup'
            break
        case FormattingNode.FormattingType.SUBSCRIPT:
            tag = 'sub'
            break
        }

        context.writer.writeStartElement(tag)

        // TODO: save the tag in context
    }

    void doPost(DocumentContext context, Node textNode) {
        context.writer.writeEndElement()
    }
}
