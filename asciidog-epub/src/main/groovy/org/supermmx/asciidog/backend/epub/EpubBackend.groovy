package org.supermmx.asciidog.backend.epub

import org.supermmx.asciidog.backend.html5.Html5Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.ast.Document

import groovy.util.logging.Slf4j

@Slf4j
class EpubBackend extends Html5Backend {
    EpubBackend() {
        super()

        id = 'epub'
        ext = '.epub'
    }

    @Override
    void doStartRendering(DocumentContext context) {
        // always chunking for epub
        context.attrContainer.setSystemAttribute(Document.OUTPUT_CHUNKED, true.toString())

        // set the chunk extension
        context.chunkExt = '.xhtml'

        log.info "chunk ext = ${context.chunkExt}"
    }

    @Override
    void doEndRendering(DocumentContext context) {
        // find all the chunks
        context.chunkingStrategy.chunks.each { chunk ->
            println "Chunk Name: ${chunk.name}"
        }
    }
}
