package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.Renderer
import org.supermmx.asciidog.converter.DocumentWalker
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.converter.DocumentTraverseListenerAdapter

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.lang.reflect.Proxy

class Html5Renderer extends DocumentTraverseListenerAdapter implements Renderer {

    private XMLStreamWriter writer

    Html5Renderer(Map<String, Object> options) {
    }

    void renderDocument(Document doc, OutputStream os) {
        def xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(os, 'UTF-8')

        PrettyPrintHandler handler = new PrettyPrintHandler(xmlWriter);
        writer = (XMLStreamWriter) Proxy.newProxyInstance(
            XMLStreamWriter.class.getClassLoader(),
            [ XMLStreamWriter.class ] as Class[] ,
            handler);

        DocumentWalker walker = new DocumentWalker(this)

        walker.traverse(doc)

        /*
        def config = new TemplateConfiguration()
        config.with {
            autoNewLine = true
            autoIndent = true
            autoIndentString = '  '
            autoEscape = true
        }

        def engine = new MarkupTemplateEngine(getClass().getClassLoader(), config)
        def template = engine.createTemplateByPath('org/supermmx/asciidog/backend/html5/html5.groovy')
        def model = [:]
        model['doc'] = doc
        def output = template.make(model)

        output.writeTo(new OutputStreamWriter(os))
        */
    }

    void enterDocument(DocumentContext context, Document document) {
        writer.with {
            // no xml declaration
            //writeStartDocument()

            writeDTD('<!DOCTYPE html>')

            writeStartElement('html')
            writeDefaultNamespace('http://www.w3.org/1999/xhtml')

            // head
            writeStartElement('head')

            writeStartElement('meta')
            writeAttribute('charset', 'UTF-8')
            writeEndElement()

            writeStartElement('title')
            writeCharacters(document.header.title)
            writeEndElement()

            // head
            writeEndElement()

            // body
            writeStartElement('body')
        }
    }

    void exitDocument(DocumentContext context, Document document) {
        writer.with {
            // body
            writeEndElement()

            // html
            writeEndElement()

            writeEndDocument()
        }
    }

    void enterDocumentHeader(DocumentContext context, Header header) {
        writer.with {
            writeStartElement('h1')
            writeCharacters(header.title)
            writeEndElement()
        }
    }
    void exitDocumentHeader(DocumentContext context, Header header) {
    }

    void enterPreamble(DocumentContext context, Preamble preamble) {
    }
    void exitPreamble(DocumentContext context, Preamble preamble) {
    }

    void enterSection(DocumentContext context, Section section) {
        writer.with {
            writeStartElement('h2')
            writeAttribute('id', section.id)
            writeCharacters(section.title)
            writeEndElement()
        }
    }
    void exitSection(DocumentContext context, Section section) {
    }

    void enterList(DocumentContext context, AdocList list) {
    }
    void exitList(DocumentContext context, AdocList list) {
    }

    void enterParagraph(DocumentContext context, Paragraph para) {
        writer.writeStartElement('p')
    }
    void exitParagraph(DocumentContext context, Paragraph para) {
        writer.writeEndElement()
    }

    void enterInline(DocumentContext context, Inline inline) {
        def tag = ''

        switch (inline.type) {
        case Node.Type.INLINE_TEXT:
            writer.writeCharacters(inline.text)
            break
        case Node.Type.INLINE_FORMATTED_TEXT:
            switch (inline.formattingType) {
            case FormattingNode.FormattingType.STRONG:
                tag = 'strong'
                break
            case FormattingNode.FormattingType.EMPHASIS:
                tag = 'em'
                break
            case FormattingNode.FormattingType.MONOSPACED:
                tag = ''
                break
            case FormattingNode.FormattingType.SUPERSCRIPT:
                tag = 'sup'
                break
            case FormattingNode.FormattingType.SUBSCRIPT:
                tag = 'sub'
                break
            }
            break
        case Node.Type.INLINE_ATTRIBUTE_REFERENCE:
            break
        case Node.Type.INLINE_CROSS_REFERENCE:
            writer.writeStartElement('a')
            writer.writeAttribute('href', "#${inline.xrefId}")
            writer.writeCharacters(context.document.references[(inline.xrefId)].title)
            writer.writeEndElement()
            break
        }

        if (tag) {
            writer.writeStartElement(tag)
        }
    }
    void exitInline(DocumentContext context, Inline inline) {
        def tag = ''

        switch (inline.type) {
        case Node.Type.INLINE_FORMATTED_TEXT:
            switch (inline.formattingType) {
            case FormattingNode.FormattingType.STRONG:
                tag = 'strong'
                break
            case FormattingNode.FormattingType.EMPHASIS:
                tag = 'em'
                break
            case FormattingNode.FormattingType.MONOSPACED:
                tag = ''
                break
            case FormattingNode.FormattingType.SUPERSCRIPT:
                tag = 'sup'
                break
            case FormattingNode.FormattingType.SUBSCRIPT:
                tag = 'sub'
                break
            }
            break
        }

        if (tag) {
            writer.writeEndElement()
        }
    }
}
