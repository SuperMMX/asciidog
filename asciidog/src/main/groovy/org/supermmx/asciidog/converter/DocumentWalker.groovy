package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble

class DocumentWalker {
    DocumentContext context
    DocumentTraverseListener listener

    DocumentWalker(DocumentTraverseListener listener) {
        this.listener = listener
        context = new DocumentContext()
    }

    void traverse(Document document) {
        context.document = document

        listener.enterDocument(context, document)

        // header
        traverseDocumentHeader(document.header)

        // preamble
        traversePreamble(document.preamble)

        // sections
        document.blocks.each { section ->
            traverseSection(section)
        }

        listener.exitDocument(context, document)
    }

    void traverseDocumentHeader(Header header) {
        listener.enterDocumentHeader(context, header)

        // attributes
        header.blocks.each { attr ->
            context.attrContainer.setAttribute(attr.name, attr.value)
            // no need to traverse attributes in backends
        }

        listener.exitDocumentHeader(context, header)
    }

    void traversePreamble(Preamble preamble) {
        listener.enterPreamble(context, preamble)

        listener.exitPreamble(context, preamble)
    }

    protected void traverseBlock(Block block) {
        switch (block.type) {
        case Node.Type.ORDERED_LIST:
        case Node.Type.UNORDERED_LIST:
            traverseList(block)
        break
        case Node.Type.PARAGRAPH:
            traverseParagraph(block)
        break
        case Node.Type.SECTION:
            traverseSection(block)
        break
        }
    }

    void traverseSection(Section section) {
        listener.enterSection(context, section)

        section.blocks.each { block ->
            traverseBlock(block)
        }

        listener.exitSection(context, section)
    }

    void traverseParagraph(Paragraph paragraph) {
        listener.enterParagraph(context, paragraph)

        // inlines
        traverseInlineContainer(paragraph)

        listener.exitParagraph(context, paragraph)
    }

    void traverseList(AdocList list) {
        listener.enterList(context, list)

        // list items

        listener.exitList(context, list)
    }

    protected void traverseInlineContainer(InlineContainer container) {
        container.inlineNodes.each { inline ->
            traverseInline(inline)
        }
    }

    void traverseInline(Inline inline) {
        listener.enterInline(context, inline)

        listener.exitInline(context, inline)
    }
}
