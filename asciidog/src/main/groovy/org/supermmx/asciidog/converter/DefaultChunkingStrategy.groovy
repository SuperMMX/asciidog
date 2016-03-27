package org.supermmx.asciidog.converter

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Document

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class DefaultChunkingStrategy extends AbstractChunkingStrategy {
    DefaultChunkingStrategy(DocumentContext context) {
        super(context)
    }

    @Override
    protected boolean doCheckChunkingPoint(Block block) {
        def level = context.attrContainer.getAttribute(Document.OUTPUT_CHUNKING_LEVEL).value

        return (block.type == Node.Type.SECTION
                && (level == -1 || block.level <= level))
    }
}
