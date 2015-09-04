package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document

class OutputChunk {
    String base
    boolean chunked
    Block block

    String getName() {
        def name = null

        if (block in Document) {
            name = 'index'
        } else {
            name = block.id
        }

        // attribute 'chunk-name'

        // id

        return name
    }
}
