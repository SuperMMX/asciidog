package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * The template-based chunk renderer
 */
class TemplateChunkRenderer implements ChunkRenderer {

    @Override
    void pre(DocumentContext context, Block block) {
        renderTemplate(context, block, 'pre')
    }

    @Override
    void post(DocumentContext context, Block block) {
        renderTemplate(context, block, 'post')
    }

    void renderTemplate(DocumentContext context, Block block, String suffix) {
        def chunkNode =  new Node(type: Node.Type.CHUNK)

        def template = context.backend.getTemplate(context, chunkNode, suffix)
        if (template == null) {
            return
        }

        def content = template.make([ context: context, node: block ])

        context.writer.write(content)
    }
}
