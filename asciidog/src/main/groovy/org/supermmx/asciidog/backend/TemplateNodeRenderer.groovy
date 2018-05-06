package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * The template-based node renderer
 */
class TemplateNodeRenderer extends AbstractLeafNodeRenderer {
    @Override
    boolean accept(Node node) {
        // accept any nodes
        return true
    }

    @Override
    void doPre(DocumentContext context, Node node) {
        renderTemplate(context, node, 'pre')
    }

    @Override
    void doRender(DocumentContext context, Node node) {
        renderTemplate(context, node, '')
    }

    @Override
    void doPost(DocumentContext context, Node node) {
        renderTemplate(context, node, 'post')
    }

    void renderTemplate(DocumentContext context, Node node, String suffix) {
        def template = context.backend.getTemplate(context, node, suffix)
        if (template == null) {
            return
        }
        def content = template.make([ context: context, node: node ])

        context.writer.write(content)
    }
}
