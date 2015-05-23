package org.supermmx.asciidog.backend.pdf

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.backend.Renderer
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section

import com.craigburke.document.builder.PdfDocumentBuilder

/**
 * PDF Renderer
 */
class PdfRenderer implements Renderer {
    private AttributeContainer attrs = new AttributeContainer()

    PdfRenderer(Map<String, Object> options) {
    }

    void renderDocument(Document doc, OutputStream os) {
        PdfDocumentBuilder builder = new PdfDocumentBuilder(os)

        builder.create {
            document {
                documentHeader(builder, doc.header)

                blocks(builder, doc.blocks)
            }
        }
    }

    private void documentHeader(def builder, def header) {
        builder.with {
            heading1 header.title, align: 'center'

            paragraph {
                header.authors.each { author ->
                    text "${author.firstname} ${author.lastname}"
                }
            }

            header.blocks.each { attr ->
                attrs.setAttribute(attr.name, attr.value)
            }
        }
    }

    private void blocks(def builder, def blocks) {
        blocks.eachWithIndex { block, index ->
            switch (block.type) {
            case Node.Type.SECTION:
                section(builder, block)
                break
            case Node.Type.ORDERED_LIST:
                olist(builder, block)
                break
            case Node.Type.UNORDERED_LIST:
                ulist(builder, block)
                break
            case Node.Type.PARAGRAPH:
                paragraph(builder, block)
                break
            }
        }
    }

    private void section(def builder, def sec) {
        switch (sec.level) {
        case 0:
            builder.heading1 sec.title
            break
        case 1:
            builder.heading2 sec.title
            break
        case 2:
            builder.heading3 sec.title
            break
        case 3:
            builder.heading4 sec.title
            break
        case 4:
            builder.heading5 sec.title
            break
        }

        blocks(builder, sec.blocks)
    }

    private void olist(def builder, def block) {
    }

    private void ulist(def builder, def block) {
    }

    private void paragraph(def builder, def block) {
        builder.paragraph {
            inlineContainer(builder, block)
        }
    }

    private void inlineContainer(def builder, InlineContainer container) {
        container.inlineNodes.each { inline ->
            inlineNode(builder, inline)
        }
    }

    private void inlineNode(def builder, Inline inline) {
        switch (inline.type) {
        case Node.Type.INLINE_ATTRIBUTE_REFERENCE:
            attributeReference(builder, inline)
            break
        case Node.Type.INLINE_TEXT:
            builder.text inline.text
            break
        case Node.Type.INLINE_FORMATTED_TEXT:
            formatText(builder, inline)
            break
        }
    }

    private void formatText(def builder, FormattingNode tfNode) {
        builder.with {
        switch (tfNode.formattingType) {
        case FormattingNode.Type.STRONG:
            font.bold = true
            inlineContainer(builder, tfNode)
            font.bold = false
            break
        case FormattingNode.Type.EMPHASIS:
            break
        }
        }
    }

    private void attributeReference(def builder, AttributeReferenceNode attrRef) {
        def name = attrRef.name
        def attr = attrs.getAttribute(name)

        switch (attr.type) {
        case Attribute.ValueType.INLINES:
            attr.value.each { inline ->
                inlineNode(builder, inline)
            }
            break
        default:
            builder.text attr.value
            break
        }
    }
}

