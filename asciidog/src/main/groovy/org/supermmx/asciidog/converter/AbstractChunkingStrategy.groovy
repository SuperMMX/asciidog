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
    protected LinkedHashMap<Long, OutputChunk> chunkMap = [:]

    AbstractChunkingStrategy(DocumentContext context) {
        this.context = context;
    }

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
                chunkMap[(block.seq)] = chunk
            }

            // deep-first, only blocks
            block.blocks.each { child ->
                process(child)
            }
        }

        process(context.document)
    }

    @Override
    OutputChunk getChunk(Block block) {
        OutputChunk chunk = null
        if (!chunkMap.containsKey(block.seq)) {
            if (isChunkingPoint(block)) {
                chunk = createChunk(block)
            }
            chunkMap[(block.seq)] = chunk
        } else {
            chunk = chunkMap[(block.seq)]
        }

        return chunk
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

        return getChunk(block)
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

    /**
     * Check whether need to chunk on the specified block
     */
    protected boolean isChunkingPoint(Block block) {
        def createChunk = false

        // whether it is chunked or not
        def chunked = context.attrContainer.getAttribute(Document.OUTPUT_CHUNKED).value
        def isStream = context.attrContainer.getAttribute(Document.OUTPUT_STREAM).value
        def type = block.type

        if (type == Node.Type.DOCUMENT) {
            // always create a chunk for document
            createChunk = true
        } else if (chunked && !isStream) {
            // no need to create chunk for streaming
            createChunk = doCheckChunkingPoint(block)
        }

        return createChunk
    }

    /**
     * Extra chunking point checking beside the common ones
     */
    protected abstract boolean doCheckChunkingPoint(Block block)

    /**
     * Actually create the chunk for the block
     */
    protected OutputChunk createChunk(Block block) {
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
        chunkMap[(block.seq)] = chunk

        return chunk;
    }
}
