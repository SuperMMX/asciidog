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
import org.supermmx.asciidog.ast.Resource
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.LeafNodeRenderer

import groovy.util.logging.Slf4j

import java.io.OutputStreamWriter
import java.nio.file.StandardCopyOption
import java.nio.file.Files
import java.nio.file.Paths

@Slf4j
class DocumentWalker {
    void traverse(Document document, Backend backend, DocumentContext context) {
        context.document = document
        context.backend = backend

        backend.startRendering(context)

        context.chunkingStrategy = new DefaultChunkingStrategy(context)

        traverseBlock(context, document)

        // end the last chunk
        endChunk(context)

        // copy local resources before ending rendering
        copyResources(context)

        backend.endRendering(context)
    }

    /**
     * Copy local resources to the destination directory
     */
    protected void copyResources(DocumentContext context) {
        def backend = context.backend
        def doc = context.document

        // copy resources to output directory
        def inputDir = doc.attrs?.inputFile?.parentFile

        if (inputDir != null) {
            // chunk path inside the output dir
            def chunkPath = backend.getChunkPath(context)
            def chunkDir = null
            if (chunkPath == null || chunkPath.length() == 0) {
                chunkDir = context.outputDir
            } else {
                chunkDir = new File(context.outputDir, chunkPath)
            }

            doc.resources.each { res ->
                def is = null
                if (res.source == Resource.Source.CLASSPATH) {
                    log.info "resource path = ${res.path}"
                    is = this.class.getResourceAsStream(res.path)
                } else {
                    is = new File(inputDir.toPath().resolve(res.path)).newInputStream()
                }

                def destFile = new File(chunkDir, res.destPath)
                if (!destFile.parentFile.exists()) {
                    destFile.parentFile.mkdirs()
                }

                Files.copy(is,
                           destFile.toPath(),
                           StandardCopyOption.REPLACE_EXISTING)

                is.close()
            }
        }
    }

    protected void traverseBlock(DocumentContext context, Block block) {
        // chunking
        startChunk(context, block)

        def backend = context.backend

        if (block.type.isAction) {
            // action nodes
            switch (block.type) {
            case Node.Type.DEFINE_ATTRIBUTE:
                def attr = (AttributeEntry)block
                context.attrContainer.setAttribute(attr.name, attr.children)
                break
            }

            return
        }

        // get the renderer
        def renderer = backend.getRenderer(block)

        // pre
        log.debug "Pre rendering block: type: ${block.type}, title: ${block.title}, renderer = ${renderer}"
        renderer?.pre(context, block)

        if (renderer in LeafNodeRenderer) {
            log.debug 'Rendering block: type: {}, title: {}, renderer = {}', block.type, block.title, renderer
            renderer?.render(context, block)
        }

        // transverse the child blocks or inlines

        if (block in InlineContainer) {
            // inline container
            traverseInlineContainer(context, block)
        } else {
            // normal blocks
            block.children.each { childBlock ->
                traverseBlock(context, (Block)childBlock)
            }
        }

        // post
        log.debug "Post rendering block: type: ${block.type}, title: ${block.title}"
        renderer?.post(context, block)
    }

    protected void traverseInlineContainer(DocumentContext context, InlineContainer container) {
        container.children.each { inline ->
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

        def renderer = backend.getRenderer(inline)

        log.debug "Pre rendering inline: type: ${inline.type}"
        renderer?.pre(context, inline)

        if (inline.children.size() > 0) {
            traverseInlineContainer(context, inline)
        } else {
            log.debug "Rendering inline: type: ${inline.type}"

            renderer = backend.getInlineRenderer(inline)
            renderer?.render(context, inline)
        }

        log.debug "Post rendering inline: type: ${inline.type}"
        renderer?.post(context, inline)
    }

    protected void startChunk(DocumentContext context, Block block) {
        def chunk = context.chunkingStrategy.getChunk(block)
        if (chunk == null) {
            return
        }

        // end previous chunk
        endChunk(context)

        // create new chunk
        context.chunk = chunk

        if (!context.attrContainer.getAttribute(Document.OUTPUT_STREAM).value) {
            def filePath = chunk.fileName
            // the directory inside the output directory
            def chunkPath = context.backend.getChunkPath(context)
            if (chunkPath != null) {
                filePath = chunkPath + File.separator + filePath
            }

            def chunkFile = new File(context.outputDir, filePath)
            if (!chunkFile.parentFile.exists()) {
                chunkFile.parentFile.mkdirs()
            }

            log.info "Create chunk: block type: ${block.type}, file: ${chunkFile}"

            context.outputStream = chunkFile.newOutputStream()
        }

        // render the chunk
        def renderer = context.backend.getChunkRenderer()

        renderer?.pre(context, block)
    }

    protected void endChunk(DocumentContext context) {
        def chunk = context.chunk
        if (chunk != null) {
            log.info "End chunk: block type: ${chunk.block.type}"

            def renderer = context.backend.getChunkRenderer()

            renderer?.post(context, chunk.block)

            if (!context.attrContainer.getAttribute(Document.OUTPUT_STREAM).value) {
                context.outputStream.close()
            }
        }
    }
}
