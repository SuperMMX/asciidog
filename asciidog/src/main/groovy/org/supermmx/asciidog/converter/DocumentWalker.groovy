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

import groovy.util.logging.Slf4j

@Slf4j
class DocumentWalker {
    void traverse(Document document, Backend backend, OutputStream os) {
        DocumentContext context = new DocumentContext(document: document,
                                                      backend: backend,
                                                      outputStream: os)
        backend.documentRenderer.pre(context, document)

        // header
        traverseDocumentHeader(context, document.header)
        // preamble
        traversePreamble(context, document.preamble)

        // sections
        document.blocks.each { block ->
            traverseBlock(context, block)
        }

        backend.documentRenderer.post(context, document)

    }

    void traverseDocumentHeader(DocumentContext context, Header header) {
        context.backend.headerRenderer.pre(context, header)

        // attributes
        header.blocks.each { attr ->
            context.attrContainer.setAttribute(attr.name, attr.value)

            // should not render anything
            //context.backend.setAttribute(context, attr.name, attr.value)
        }

        context.backend.headerRenderer.post(context, header)
    }

    void traversePreamble(DocumentContext context, Preamble preamble) {
        context.backend.preambleRenderer.pre(context, preamble)

        preamble.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.preambleRenderer.post(context, preamble)
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
        context.backend.sectionRenderer.pre(context, section)

        section.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.sectionRenderer.post(context, section)
    }

    void traverseParagraph(DocumentContext context, Paragraph paragraph) {
        context.backend.paragraphRenderer.pre(context, paragraph)

        // inlines
        traverseInlineContainer(context, paragraph)

        context.backend.paragraphRenderer.post(context, paragraph)
    }

    void traverseList(DocumentContext context, AdocList list) {
        context.backend.listRenderer.pre(context, list)

        // list items
        list.blocks.each { item ->
            traverseListItem(context, item)
        }

        context.backend.listRenderer.post(context, list)
    }

    void traverseListItem(DocumentContext context, ListItem item) {
        context.backend.listItemRenderer.pre(context, item)

        item.blocks.each { block ->
            traverseBlock(context, block)
        }

        context.backend.listItemRenderer.post(context, item)
    }

    protected void traverseInlineContainer(DocumentContext context, InlineContainer container) {
        container.inlineNodes.each { inline ->
            traverseInline(context, inline)
        }
    }

    void traverseInline(DocumentContext context, Inline inline) {
        if (inline.type == Node.Type.ATTRIBUTE_REFERENCE) {
            def name = inline.name
            def attr = context.attrContainer.getAttribute(name)
            if (attr.type == Attribute.ValueType.INLINES) {
                attr.value.each { attrInline ->
                    traverseInline(context, attrInline)
                }
            } else {
                //context.backend.renderText(context, attr.value)
            }
        } else if (inline.type == Node.Type.TEXT) {
            // only render the content
            context.backend.inlineTextRenderer.render(context, inline)
        } else if (inline.type == Node.Type.FORMATTING) {
            context.backend.inlineFormattingRenderer.pre(context, inline)

            traverseInlineContainer(context, inline)

            context.backend.inlineFormattingRenderer.post(context, inline)
        } else if (inline.type == Node.Type.CROSS_REFERENCE) {
            context.backend.inlineXrefRenderer.pre(context, inline)

            context.backend.inlineXrefRenderer.render(context, inline)

            context.backend.inlineXrefRenderer.post(context, inline)
        }

    }
}
