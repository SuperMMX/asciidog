package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(excludes=['prev', 'next'])
class OutputChunk {
    /**
     * The chunk index
     */
    int index

    OutputChunk prev
    OutputChunk next

    /**
     * The corresponding block
     */
    Block block

    /**
     * The chunk output file name
     */
    String fileName

    /**
     * Get the chunk name whic is used as
     * the part of the output file
     */
    String getName() {
        def name = null

        if (block in Document) {
            name = 'index'
        } else {
            name = block.attributes[(Node.ATTRIBUTE_CHUNK_NAME)]
        }

        if (name == null) {
            name = block.id
        }

        return name
    }
}
