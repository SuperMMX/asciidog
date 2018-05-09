package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

import groovy.util.logging.Slf4j

/**
 * The template-based node renderer
 */
@Slf4j
class TemplateNodeRenderer extends AbstractLeafNodeRenderer {
    @Override
    boolean accept(Node node) {
        // accept any nodes
        return true
    }

    @Override
    void doPre(DocumentContext context, Node node) {
        context.backend.renderNode(context, node, node, 'pre')
    }

    @Override
    void doRender(DocumentContext context, Node node) {
        context.backend.renderNode(context, node, node, '')
    }

    @Override
    void doPost(DocumentContext context, Node node) {
        context.backend.renderNode(context, node, node, 'post')
    }
}
