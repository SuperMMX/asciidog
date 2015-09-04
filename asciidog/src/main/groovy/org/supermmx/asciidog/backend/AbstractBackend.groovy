package org.supermmx.asciidog.backend

import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.converter.DocumentContext

/**
 * Abstract backend
 */
abstract class AbstractBackend implements Backend {
    String id
    String ext

    protected Map<Node.Type, NodeRenderer> renderers = [:]

    ChunkRenderer chunkRenderer

    NodeRenderer getRenderer(Node.Type nodeType) {
        def renderer = null

        while (renderer == null && nodeType != null) {
            renderer = renderers[(nodeType)]
            nodeType = nodeType.parent
        }
        return renderer
    }

    LeafNodeRenderer getInlineRenderer(Node.Type nodeType) {
        if (!nodeType.isInline()) {
            throw new IllegalArgumentException("Node type \"${nodeType}\" is not an inline type")
        }

        def renderer = renderers[(nodeType)]
        if (!(renderer in LeafNodeRenderer)) {
            renderer = null
        }

        return renderer
    }

    void registerRenderer(Node.Type nodeType, NodeRenderer renderer) {
        renderers[(nodeType)] = renderer
    }
}
