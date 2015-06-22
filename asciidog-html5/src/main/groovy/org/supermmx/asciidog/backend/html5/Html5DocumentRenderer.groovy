package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.lang.reflect.Proxy

class Html5DocumentRenderer extends AbstractNodeRenderer {
    Html5DocumentRenderer() {
        nodeType = Node.Type.DOCUMENT
    }

    void doPre(DocumentContext context, Node node) {
        Document document = (Document) node

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
    }

    void doPost(DocumentContext context, Node node) {
        context.writer.with {
            // body
            writeEndElement()

            // html
            writeEndElement()

            writeEndDocument()
        }
    }
}
