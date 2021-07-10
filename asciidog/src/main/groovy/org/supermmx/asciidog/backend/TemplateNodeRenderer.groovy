package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

/**
 * The template-based node renderer
 */
@Slf4j
class TemplateNodeRenderer extends AbstractLeafNodeRenderer {
    private static final TEMPLATE_BLOCK_NODE = new Block()

    @Override
    boolean accept(Node node) {
        // accept any nodes
        return true
    }

    @Override
    void doPre(DocumentContext context, Node node) {
        // only for content blocks
        if (toRenderHeader(node)) {
            // the wrapping block element, id, and other necessary attributes
            context.backend.renderNode(context, TEMPLATE_BLOCK_NODE, node, 'header')

            // render the block title
            context.backend.renderNode(context, TEMPLATE_BLOCK_NODE, node, 'title')
        }

        // the real node element
        context.backend.renderNode(context, node, node, 'pre')
    }

    @Override
    void doRender(DocumentContext context, Node node) {
        context.backend.renderNode(context, node, node, '')
    }

    @Override
    void doPost(DocumentContext context, Node node) {
        context.backend.renderNode(context, node, node, 'post')

        // only for content blocks
        if (toRenderHeader(node)) {
            // the block element, id, and other necessary attributes
            context.backend.renderNode(context, TEMPLATE_BLOCK_NODE, node, 'header_post')
        }

    }

    private boolean toRenderHeader(Node node) {
        return node.type.isType(Node.Type.BLOCK) && !node.type.isType(Node.Type.LIST_ITEM)
    }
}
