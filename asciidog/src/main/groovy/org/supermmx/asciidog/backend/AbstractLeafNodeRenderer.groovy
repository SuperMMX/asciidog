package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * Abstract leaf node renderer
 */
abstract class AbstractLeafNodeRenderer extends AbstractNodeRenderer implements LeafNodeRenderer {
    void render(DocumentContext context, Node node) {
        if (accept(node)) {
            doRender(context, node)
        }
    }

    abstract void doRender(DocumentContext context, Node node)
}
