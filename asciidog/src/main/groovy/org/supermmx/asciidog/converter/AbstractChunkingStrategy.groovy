package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node

import groovy.util.logging.Slf4j

/**
 * The strategy to decide when to create the chunk,
 * and what's the name of the chunk
 */
@Slf4j
abstract class AbstractChunkingStrategy implements ChunkingStrategy {
    List<OutputChunk> chunks = []

    protected DocumentContext context

    protected int index = -1
    protected LinkedHashMap<Block, OutputChunk> chunkMap = [:]

    /**
     * Checking all blocks and create the corresponding chunks
     */
    protected void initialize(DocumentContext context) {
        this.context = context

        def process

        process = { block ->
            if (isChunkingPoint(block)) {
                def chunkIndex = chunks.size()
                def chunk = new OutputChunk(block: block,
                                            index: chunkIndex)
                chunk.fileName = getChunkFileName(chunk)

                if (chunkIndex > 0) {
                   def prev = chunks[chunkIndex - 1]
                   prev.next = chunk
                   chunk.prev = prev
                }

                chunks << chunk
                chunkMap[(block)] = chunk
            }

            // deep-first, only blocks
            block.blocks.each { child ->
                process(child)
            }
        }

        process(context.document)
    }
    
    protected abstract boolean isChunkingPoint(Block block)

    @Override
    OutputChunk getChunk(Block block) {
        return chunkMap[(block)]
    }

    @Override
    OutputChunk findChunk(Node node) {
        // find the parent block
        def block = null
        while (!(node in Block)) {
            node = node.parent
        }

        block = node
        while (!isChunkingPoint(block)) {
            // TODO: checking previous sibling blocks

            // checking parent
            block = block.parent
        }

        return chunkMap[(block)]
    }

    /**
     * Get the output file name for the chunk
     */
    protected getChunkFileName(OutputChunk chunk) {
        // construct the file name for the chunk

        def name = ''
        if (context.attrContainer.getAttribute(Document.OUTPUT_CHUNKED).value) {
            name = chunk.getName()
        } else {
            name = context.attrContainer.getAttribute(Document.OUTPUT_BASE).value
        }

        def ext = context.chunkExt

        if (ext == null) {
            ext = context.backend.ext
        }

        name += ext

        return name
    }
}
