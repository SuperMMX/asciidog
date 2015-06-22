package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

@Slf4j
class Html5ListRenderer extends AbstractNodeRenderer {
    Html5ListRenderer() {
        nodeType = Node.Type.LIST
    }

    @Override
    boolean accept(Node list) {
        return list.type == Node.Type.ORDERED_LIST || list.type == Node.Type.UNORDERED_LIST
    }

    @Override
    void doPre(DocumentContext context, Node list) {
        def tag = ''

        if (list.type == Node.Type.ORDERED_LIST) {
            tag = 'ol'
        } else if (list.type == Node.Type.UNORDERED_LIST) {
            tag = 'ul'
        }

        if (tag) {
            // TODO: save the tag into context
            context.writer.writeStartElement(tag)
        }
    }

    @Override
    void doPost(DocumentContext context, Node list) {
        context.writer.writeEndElement()
    }
}
