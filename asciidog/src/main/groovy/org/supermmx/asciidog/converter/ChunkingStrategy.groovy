package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Block

/**
 * The strategy to decide when to create the chunk,
 * and what's the name of the chunk
 */
interface ChunkingStrategy {
    /**
     * Get the chunk from the block
     *
     * @return new chunk if needed, null otherwise
     */
    OutputChunk getChunk(Block block)
}
