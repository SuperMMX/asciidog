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
    static enum Step {
        PRE, CONTENT, POST
    }

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
     * The renderer to render something before or after the document
     * (not the document content)
     */
    NodeRenderer getDocumentRenderer()
    NodeRenderer getHeaderRenderer()
    NodeRenderer getPreambleRenderer()
    NodeRenderer getSectionRenderer()
    NodeRenderer getListRenderer()
    NodeRenderer getListItemRenderer()
    NodeRenderer getParagraphRenderer()

    LeafNodeRenderer getInlineTextRenderer()
    NodeRenderer getInlineFormattingRenderer()
    NodeRenderer getInlineXrefRenderer()
}