package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.converter.DocumentContext

/**
 * A renderer that renders header/footer of a chunk
 */
interface ChunkRenderer {
    /**
     * Before start rendering the chunk
     */
    void pre(DocumentContext context, Block block)

    /**
     * After rendering the chunk
     */
    void post(DocumentContext context, Block block)
}
