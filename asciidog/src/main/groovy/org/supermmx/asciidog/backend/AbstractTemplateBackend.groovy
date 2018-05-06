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
     * Get the template for the node and the suffix
     *
     * @param context the document context
     * @param node the node to render
     * @param suffix the suffix for pre/post rendering
     *
     * @return the template to render the node, null if not found
     */
    protected Template getTemplate(DocumentContext context, Node node, String suffix) {
        return TemplateManager.instance.getTemplate(context, node, suffix, templateExt)
    }
}
