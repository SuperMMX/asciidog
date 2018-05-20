package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

/**
 * The template-based chunk renderer
 */
@Slf4j
class TemplateChunkRenderer implements ChunkRenderer {
    /**
     * Used to get the chunk templates
     */
    private static final Node CHUNK_NODE =  new Node(type: Node.Type.CHUNK)

    @Override
    void pre(DocumentContext context, Block block) {
        context.writer = new OutputStreamWriter(context.outputStream, 'UTF-8')

        context.backend.renderNode(context, CHUNK_NODE, block, 'pre')
    }

    @Override
    void post(DocumentContext context, Block block) {
        context.backend.renderNode(context, CHUNK_NODE, block, 'post')

        context.writer.close()
    }
}
