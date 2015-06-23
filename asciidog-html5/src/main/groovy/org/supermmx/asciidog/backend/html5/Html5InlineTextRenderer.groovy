package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractLeafNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

class Html5InlineTextRenderer extends AbstractLeafNodeRenderer {
    Html5InlineTextRenderer() {
        nodeType = Node.Type.TEXT
    }

    void doPre(DocumentContext context, Node textNode) {
    }

    void doRender(DocumentContext context, Node textNode) {
        context.writer.writeCharacters(textNode.text)
    }

    void doPost(DocumentContext context, Node textNode) {
    }
}
