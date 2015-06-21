package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import java.lang.reflect.Proxy

class Html5HeaderRenderer extends AbstractNodeRenderer {
    Html5HeaderRenderer() {
        nodeType = Node.Type.DOCUMENT_HEADER
    }

    void doPre(DocumentContext context, Node node) {
        Header header = (Header) node

        context.writer.with {
            writeStartElement('h1')
            writeCharacters(header.title)
        }
    }

    void doPost(DocumentContext context, Node node) {
        context.writer.writeEndElement()
    }
}
