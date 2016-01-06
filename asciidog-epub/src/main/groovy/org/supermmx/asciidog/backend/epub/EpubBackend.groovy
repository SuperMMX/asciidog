package org.supermmx.asciidog.backend.epub

import org.supermmx.asciidog.backend.html5.Html5Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.ast.Document

import org.supermmx.epug.creator.EpubCreator
import org.supermmx.epug.epub.dcmi.DcElement
import org.supermmx.epug.epub.dcmi.DcesTerm

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

        def epubCreator = new EpubCreator()

        def rendition = epubCreator.publication.rendition

        // Unique Identifier
        // the id reference is always 'epub-id'
        rendition.uniqueIdentifier = 'epub-id'
        def idAttr= context.attrContainer.getAttribute('epub-id')
        def id = idAttr ? idAttr.value : doc.title
        rendition.metadata.dcTerms << new DcElement(id: 'epub-id', term: DcesTerm.identifier, value: id)

        // title
        rendition.metadata.dcTerms << new DcElement(term: DcesTerm.title, value: doc.title)

        // language
        def langAttr = context.attrContainer.getAttribute('language')
        def lang= langAttr ? langAttr.value : 'en'

        rendition.metadata.dcTerms << new DcElement(term: DcesTerm.language, value: lang)

        // find all the chunks
        context.chunkingStrategy.chunks.each { chunk ->
            def chunkFile = new File(outputDir, chunk.fileName)

            epubCreator.addSpineItem(chunkFile.getAbsolutePath(), chunk.fileName, chunk.block.id, chunk.block.title)
        }

        // create the epub
        def base = context.attrContainer.getAttribute(Document.OUTPUT_BASE).value

        epubCreator.write(new File(outputDir, base + ext))
    }
}
