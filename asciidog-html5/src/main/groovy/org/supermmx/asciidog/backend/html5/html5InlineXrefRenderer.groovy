package org.supermmx.asciidog.backend.html5

import org.supermmx.asciidog.Utils
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractLeafNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Html5InlineXrefRenderer extends AbstractLeafNodeRenderer {
    Html5InlineXrefRenderer() {
        nodeType = Node.Type.CROSS_REFERENCE
    }

    void doPre(DocumentContext context, Node xrefNode) {
        def id = Utils.normalizeId(xrefNode.xrefId)
        context.xrefId = id

        context.writer.with {
            writeStartElement('a')

            // find out the target chunk file
            def file = ''
            if (context.attrContainer.getAttribute(Document.OUTPUT_CHUNKED)) {
                def targetNode = context.document.references[(id)]

                if (targetNode != null) {
                    def targetChunk = context.chunkingStrategy.findChunk(targetNode)

                    def chunk = context.chunk
                    if (chunk != targetChunk) {
                        file = context.chunkingStrategy.getChunkFileName(targetChunk)
                    }
                }
            }

            writeAttribute('href', "${file}#${id}")
        }
    }

    void doRender(DocumentContext context, Node xrefNode) {
        context.writer.writeCharacters(context.document.references[(context.xrefId)].title)
    }

    void doPost(DocumentContext context, Node xrefNode) {
        context.writer.writeEndElement()
        context.remove('xrefId')
    }
}
