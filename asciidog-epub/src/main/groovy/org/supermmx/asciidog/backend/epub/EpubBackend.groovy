package org.supermmx.asciidog.backend.epub

import org.supermmx.asciidog.backend.html5.Html5Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.ast.Document

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Metadata
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.epub.EpubWriter

import groovy.util.logging.Slf4j

@Slf4j
class EpubBackend extends Html5Backend {
    EpubBackend() {
        super()

        parentId = id

        id = 'epub'
        ext = '.epub'
    }

    @Override
    void doStartRendering(DocumentContext context) {
        // always chunking for epub
        context.attrContainer.setSystemAttribute(Document.OUTPUT_CHUNKED, true.toString())

        // set the chunk extension
        context.chunkExt = '.xhtml'
    }

    @Override
    void doEndRendering(DocumentContext context) {
        def doc = context.document
        def outputDir = context.outputDir

        // create the epub structure
        // Create new Book
        Book book = new Book();
        Metadata metadata = book.getMetadata();

        // Set the title
        metadata.addTitle(doc.header?.title);

        // find all the chunks
        context.chunkingStrategy.chunks.each { chunk ->
            def chunkFile = new File(outputDir, chunk.fileName)
            book.addSection(chunk.block.title, new Resource(chunkFile.bytes, chunk.fileName))
        }

        // create the epub
        def base = context.attrContainer.getAttribute(Document.OUTPUT_BASE).value
        // Create EpubWriter
        EpubWriter epubWriter = new EpubWriter();

        // Write the Book as Epub
        epubWriter.write(book, new FileOutputStream(new File(outputDir, base + ext)));
    }
}
