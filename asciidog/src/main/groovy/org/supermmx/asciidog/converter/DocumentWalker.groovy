package org.supermmx.asciidog.converter

import org.supermmx.asciidog.Attribute
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
import org.supermmx.asciidog.backend.Backend

class DocumentWalker {
    void traverse(Document document, Backend backend, OutputStream os) {
        DocumentContext context = new DocumentContext(document: document,
                                                      backend: backend,
                                                      outputStream: os)
        backend.renderDocument(Backend.Step.PRE, context, document)

        // header
        traverseDocumentHeader(context, document.header)

        // preamble
        traversePreamble(context, document.preamble)

        // sections
        document.blocks.each { block ->
            traverseBlock(context, block)
        }

        backend.renderDocument(Backend.Step.POST, context, document)
    }

    void traverseDocumentHeader(DocumentContext context, Header header) {
        context.backend.renderDocumentHeader(Backend.Step.PRE, context, header)

        // attributes
        header.blocks.each { attr ->
            context.attrContainer.setAttribute(attr.name, attr.value)

            // should not render anything
            context.backend.setAttribute(context, attr.name, attr.value)
        }

        context.backend.renderDocumentHeader(Backend.Step.POST, context, header)
    }

    void traversePreamble(DocumentContext context, Preamble preamble) {
        context.backend.renderPreamble(Backend.Step.PRE, context, preamble)

        preamble.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.renderPreamble(Backend.Step.POST, context, preamble)
    }

    protected void traverseBlock(DocumentContext context, Block block) {
        switch (block.type) {
        case Node.Type.ORDERED_LIST:
        case Node.Type.UNORDERED_LIST:
            traverseList(context, block)
        break
        case Node.Type.PARAGRAPH:
            traverseParagraph(context, block)
        break
        case Node.Type.SECTION:
            traverseSection(context, block)
        break
        }
    }

    void traverseSection(DocumentContext context, Section section) {
        context.backend.renderSection(Backend.Step.PRE, context, section)

        section.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.renderSection(Backend.Step.POST, context, section)
    }

    void traverseParagraph(DocumentContext context, Paragraph paragraph) {
        context.backend.renderParagraph(Backend.Step.PRE, context, paragraph)

        // inlines
        traverseInlineContainer(context, paragraph)

        context.backend.renderParagraph(Backend.Step.POST, context, paragraph)
    }

    void traverseList(DocumentContext context, AdocList list) {
        context.backend.renderList(Backend.Step.PRE, context, list)

        // list items
        list.blocks.each { item ->
            traverseListItem(context, item)
        }

        context.backend.renderList(Backend.Step.POST, context, list)
    }

    void traverseListItem(DocumentContext context, ListItem item) {
        context.backend.renderListItem(Backend.Step.PRE, context, item)

        item.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.renderListItem(Backend.Step.POST, context, item)
    }

    protected void traverseInlineContainer(DocumentContext context, InlineContainer container) {
        container.inlineNodes.each { inline ->
            traverseInline(context, inline)
        }
    }

    void traverseInline(DocumentContext context, Inline inline) {
        if (inline.type == Node.Type.INLINE_ATTRIBUTE_REFERENCE) {
            def name = inline.name
            def attr = context.attrContainer.getAttribute(name)
            if (attr.type == Attribute.ValueType.INLINES) {
                attr.value.each { attrInline ->
                    traverseInline(context, attrInline)
                }
            } else {
                context.backend.renderText(context, attr.value)
            }
        } else if (inline.type == Node.Type.INLINE_TEXT) {
            // only render the content
            context.backend.renderInlineText(Backend.Step.CONTENT, context, inline)
        } else if (inline.type == Node.Type.INLINE_FORMATTED_TEXT) {
            context.backend.renderInlineFormatting(Backend.Step.PRE, context, inline)
            traverseInlineContainer(context, inline)
            context.backend.renderInlineFormatting(Backend.Step.POST, context, inline)
        } else if (inline.type == Node.Type.INLINE_CROSS_REFERENCE) {
            context.backend.renderInlineCrossReference(Backend.Step.PRE, context, inline)
            context.backend.renderInlineCrossReference(Backend.Step.CONTENT, context, inline)
            context.backend.renderInlineCrossReference(Backend.Step.POST, context, inline)
        }

    }
}
