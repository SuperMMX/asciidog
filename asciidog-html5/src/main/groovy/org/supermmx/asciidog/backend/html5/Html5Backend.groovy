package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.backend.NullNodeRenderer
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
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.lang.reflect.Proxy

@Slf4j
class Html5Backend extends AbstractBackend {
    Html5Backend() {
        id = 'html5'
        ext = '.html'

        documentRenderer = new Html5DocumentRenderer()
        headerRenderer = new Html5HeaderRenderer()
        preambleRenderer = new NullNodeRenderer()
        sectionRenderer = new Html5SectionRenderer()
        paragraphRenderer = new Html5ParagraphRenderer()
        listRenderer = new Html5ListRenderer()
        listItemRenderer = new Html5ListItemRenderer()

        inlineTextRenderer = new Html5InlineTextRenderer()
        inlineFormattingRenderer = new Html5InlineFormattingRenderer()
        inlineXrefRenderer = new Html5InlineXrefRenderer()
    }

    void renderDocument(Step step, DocumentContext context, Document document) {
        if (step == Step.PRE) {
            def xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(context.outputStream, 'UTF-8')

            PrettyPrintHandler handler = new PrettyPrintHandler(xmlWriter);
            def writer = (XMLStreamWriter) Proxy.newProxyInstance(
                XMLStreamWriter.class.getClassLoader(),
                [ XMLStreamWriter.class ] as Class[] ,
                handler);

            context.writer = writer

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
        } else if (step == Step.POST) {
            context.writer.with {
                // body
                writeEndElement()

                // html
                writeEndElement()

                writeEndDocument()
            }
        }
    }

    void renderDocumentHeader(Step step, DocumentContext context, Header header) {
        context.writer.with {
            if (step == Step.PRE) {
                writeStartElement('h1')
                writeCharacters(header.title)
                writeEndElement()
            }
        }
    }

    void setAttribute(DocumentContext context, String name, Object value) {
    }

    void renderPreamble(Step step, DocumentContext context, Preamble preamble) {
    }

    void renderSection(Step step, DocumentContext context, Section section) {
        context.writer.with {
            if (step == Step.PRE) {
                writeStartElement('h2')
                writeAttribute('id', section.id)
                writeCharacters(section.title)
                writeEndElement()
            }
        }
    }

    void renderList(Step step, DocumentContext context, AdocList list) {
        def tag = ''
        if (list.type == Node.Type.ORDERED_LIST) {
            tag = 'ol'
        } else if (list.type == Node.Type.UNORDERED_LIST) {
            tag = 'ul'
        }
        if (tag) {
            if (step == Step.PRE) {
                context.writer.writeStartElement(tag)
            } else if (step == Step.POST) {
                context.writer.writeEndElement()
            }
        }
    }

    void renderListItem(Step step, DocumentContext context, ListItem item) {
        context.writer.with {
            if (step == Step.PRE) {
                writeStartElement('li')
            } else if (step == Step.POST) {
                writeEndElement()
            }
        }
    }

    void renderParagraph(Step step, DocumentContext context, Paragraph paragraph) {
        context.writer.with {
            if (step == Step.PRE) {
                writeStartElement('p')
            } else if (step == Step.POST) {
                writeEndElement()
            }
        }
    }

    void renderText(DocumentContext context, String text) {
        context.writer.writeCharacters(text)
    }

    void renderInlineText(Step step, DocumentContext context, TextNode textNode) {
        if (step == Step.CONTENT) {
            context.writer.writeCharacters(textNode.text)
        }
    }

    void renderInlineFormatting(Step step, DocumentContext context, FormattingNode formattingNode) {
        if (step == Step.PRE) {
            def tag = ''

            switch (formattingNode.formattingType) {
            case FormattingNode.FormattingType.STRONG:
                tag = 'strong'
                break
            case FormattingNode.FormattingType.EMPHASIS:
                tag = 'em'
                break
            case FormattingNode.FormattingType.MONOSPACED:
                tag = 'span'
                break
            case FormattingNode.FormattingType.SUPERSCRIPT:
                tag = 'sup'
                break
            case FormattingNode.FormattingType.SUBSCRIPT:
                tag = 'sub'
                break
            }

            context.writer.writeStartElement(tag)
        } else if (step == Step.POST) {
            context.writer.writeEndElement()
        }
    }

    void renderInlineCrossReference(Step step, DocumentContext context, CrossReferenceNode xrefNode) {
        context.writer.with {
            if (step == Step.PRE) {
                writeStartElement('a')
                writeAttribute('href', "#${xrefNode.xrefId}")
            } else if (step == Step.CONTENT) {
                writeCharacters(context.document.references[(xrefNode.xrefId)].title)
            } else if (step == Step.POST) {
                writeEndElement()
            }
        }
    }
}
