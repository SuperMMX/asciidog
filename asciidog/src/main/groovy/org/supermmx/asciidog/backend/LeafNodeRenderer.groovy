package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * A renderer renders the document
 */
interface LeafNodeRenderer extends NodeRenderer {
    void render(DocumentContext context, Node node)
}
