package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

import groovy.text.Template

import groovy.util.logging.Slf4j

/**
 * The abstract class for backends with template-based rendering
 */
@Slf4j
class AbstractTemplateBackend extends AbstractBackend {
    /**
     * The template directory name in the classpath inside the backend plugin
     */
    static final String TEMPLATE_DIR = 'templates'

    /**
     * Template file extension for this backend
     */
    protected String templateExt

    /**
     * The universal node renderer to render all nodes
     */
    protected TemplateNodeRenderer nodeRenderer

    AbstractTemplateBackend() {
        chunkRenderer = new TemplateChunkRenderer()

        // just need one node renderer to render all nodes
        nodeRenderer = new TemplateNodeRenderer()

        // register template path
        def pkgPath = this.class.package.name.replaceAll('\\.', '/')
        def templatePath = "/${pkgPath}/${TEMPLATE_DIR}/"

        TemplateManager.instance.registerTemplateDirectory(id, templatePath, true)
    }

    @Override
    NodeRenderer getRenderer(Node node) {
        return nodeRenderer
    }

    @Override
    LeafNodeRenderer getInlineRenderer(Node node) {
        return nodeRenderer
    }

    /**
     * Render the node
     *
     * @param context the document context
     * @param templateNode the node to get the template
     * @param node the node to render
     * @param suffix the suffix for pre/post rendering
     */
    protected void renderNode(DocumentContext context, Node templateNode, Node node, String suffix) {
        def template = TemplateManager.instance.getTemplate(context, templateNode, suffix, templateExt)
        if (template == null) {
            return
        }

        def content = template.make([ context: context, node: node]).toString()
        content = TemplateManager.trim(content, node, suffix)

        context.writer.write(content)
    }
}
