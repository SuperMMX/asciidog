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

    void renderDocument(Step step, DocumentContext context, Document document)

    void renderDocumentHeader(Step step, DocumentContext context, Header header)

    void setAttribute(DocumentContext context, String name, Object value)

    void renderPreamble(Step step, DocumentContext context, Preamble preamble)

    void renderSection(Step step, DocumentContext context, Section section)

    void renderList(Step step, DocumentContext context, AdocList list)

    void renderListItem(Step step, DocumentContext context, ListItem item)

    void renderParagraph(Step step, DocumentContext context, Paragraph paragraph)

    // Inlines
    void renderText(DocumentContext context, String text)

    void renderInlineText(Step step, DocumentContext context, TextNode textNode)

    void renderInlineFormatting(Step step, DocumentContext context, FormattingNode formattingNode)

    void renderInlineCrossReference(Step step, DocumentContext context, CrossReferenceNode xrefNode)
}
