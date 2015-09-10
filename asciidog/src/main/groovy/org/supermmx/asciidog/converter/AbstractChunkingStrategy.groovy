package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Block

/**
 * The strategy to decide when to create the chunk,
 * and what's the name of the chunk
 */
abstract class AbstractChunkingStrategy implements ChunkingStrategy {
    protected DocumentContext context

    protected int index = -1
    protected List<OutputChunk> chunks = []

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
                if (index > 0) {
                   def prev = chunks[index - 1]
                   prev.next = chunk
                   chunk.prev = prev
                }

                chunks << chunk
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
        def chunk = null
        if (isChunkingPoint(block)) {
            index ++
            chunk = chunks[index]
        }
    }
}
