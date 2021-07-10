package org.supermmx.asciidog.backend

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
 * A backend is responsible for creating the renderer to render
 * the parsed document.
 */
interface Backend {
    /**
     * Get the parent backend id
     */
    String getParentId()

    /**
     * The backend id
     */
    String getId()

    /**
     * Get the extension for the file generated from this backend,
     * like '.html', '.pdf'
     */
    String getExt()

    /**
     * The path inside the output dir
     */
    String getChunkPath(DocumentContext context)

    /**
     * Register the renderer to this backend.
     *
     * @param renderer the renderer to render the node
     */
    void registerRenderer(NodeRenderer renderer)

    /**
     * Get the renderer for the node type
     */
    NodeRenderer getRenderer(Node node)

    /**
     * Get the inline renderer for the node type
     */
    LeafNodeRenderer getInlineRenderer(Node node)

    void addNodeRenderingProperties(DocumentContext context, Node node)

    ChunkRenderer getChunkRenderer()

    void startRendering(DocumentContext context)

    void endRendering(DocumentContext context)
}
