package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.BlockMacro
import org.supermmx.asciidog.backend.AbstractBlockMacroRenderer
import org.supermmx.asciidog.converter.DocumentContext

/**
 * The renderer for image block macro
 */
class Html5ImageBlockMacroRenderer extends AbstractBlockMacroRenderer {
    Html5ImageBlockMacroRenderer() {
        name = 'image'
    }

    @Override
    void doRender(DocumentContext context, Node node) {
        BlockMacro macroNode = (BlockMacro)node

        context.writer.writeStartElement('img')
        context.writer.writeAttribute('src', macroNode.target)
        context.writer.writeEndElement()
    }
}
