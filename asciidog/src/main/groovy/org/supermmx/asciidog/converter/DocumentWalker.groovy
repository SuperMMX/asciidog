package org.supermmx.asciidog.converter

import org.supermmx.asciidog.Attribute
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.AttributeEntry
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
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.backend.Backend

import groovy.util.logging.Slf4j

@Slf4j
class DocumentWalker {
    void traverse(Document document, Backend backend, DocumentContext context) {
        traverseBlock(context, document)

        // end chunk
        endChunk(context)
    }

    protected void traverseBlock(DocumentContext context, Block block) {
        def backend = context.backend

        // chunking
        startChunk(context, block)

        if (block.type.isAction) {
            // action nodes
            switch (block.type) {
            case Node.Type.DEFINE_ATTRIBUTE:
                def attr = (AttributeEntry)block
                context.attrContainer.setAttribute(attr.name, attr.value)
                break
            }

            return
        }

        // get the renderer
        def renderer = backend.getRenderer(block.type)

        // pre
        log.debug "Pre rendering block: type: ${block.type}, title: ${block.title}"
        renderer?.pre(context, block)

        // tranverse the child blocks or inlines

        if (block in InlineContainer) {
            // inline container
            traverseInlineContainer(context, block)
        } else {
            // normal blocks
            block.blocks.each { childBlock ->
                traverseBlock(context, childBlock)
            }
        }

        // post
        log.debug "Post rendering block: type: ${block.type}, title: ${block.title}"
        renderer?.post(context, block)
    }

    protected void traverseInlineContainer(DocumentContext context, InlineContainer container) {
        container.inlineNodes.each { inline ->
            traverseInline(context, inline)
        }
    }

    void traverseInline(DocumentContext context, Inline inline) {
        def backend = context.backend

        // expand the attribute reference
        if (inline.type == Node.Type.ATTRIBUTE_REFERENCE) {
            def name = inline.name
            def attr = context.attrContainer.getAttribute(name)
            if (attr.type == Attribute.ValueType.INLINES) {
                attr.value.each { attrInline ->
                    traverseInline(context, attrInline)
                }
            } else {
                // render the text directly?
            }

            return
        }

        // all inlines should be handled by the renderer plugin

        def renderer = backend.getRenderer(inline.type)

        log.debug "Pre rendering inline: type: ${inline.type}"
        renderer?.pre(context, inline)

        if (inline in InlineContainer) {
            traverseInlineContainer(context, inline)
        } else {
            log.debug "Rendering inline: type: ${inline.type}"

            renderer = backend.getInlineRenderer(inline.type)
            renderer?.render(context, inline)
        }

        log.debug "Post rendering inline: type: ${inline.type}"
        renderer?.post(context, inline)
    }

    protected void startChunk(DocumentContext context, Block block) {
        // whether it is chunked or not
        def chunked = context.attrContainer.getAttribute(Document.OUTPUT_CHUNKED)

        // whether to create the chunk, a chunk is always created for a document
        def createChunk = chunked

        def type = block.type

        if (type == Node.Type.DOCUMENT) {
            createChunk = true
        } else if (chunked) {
            createChunk = (block.type == Node.Type.SECTION)
        }

        if (createChunk) {

            // TODO: check previous chunk
            def previousChunk = context.chunk
            if (previousChunk != null) {
                endChunk(context)
            }

            def renderer = context.backend.getChunkRenderer()

            context.push()

            // TODO: find next chunk


            def base = context.attrContainer.getAttribute('base')

            def chunk = new OutputChunk(base:base, chunked: chunked, block: block)
            context.chunk = chunk

            // create output file
            def chunkFile = new File(context.outputDir, chunk.getName() + context.backend.ext)
            log.info "Create chunk: block type: ${block.type}, file: ${chunkFile}"

            context.outputStream = chunkFile.newOutputStream()

            renderer?.pre(context, block)
        }
    }

    protected void endChunk(DocumentContext context) {
        def chunk = context.chunk
        if (chunk != null) {
            log.info "End chunk: block type: ${chunk.block.type}"

            def renderer = context.backend.getChunkRenderer()

            renderer?.post(context, chunk.block)

            context.outputStream.close()

            context.pop()
        }
    }
}
