package org.supermmx.asciidog.backend

import org.supermmx.asciidog.Subtype
import org.supermmx.asciidog.backend.Backend
import org.supermmx.asciidog.backend.AbstractBackend
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Preamble
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.converter.DocumentContext
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

/**
 * Abstract backend
 */
@Slf4j
abstract class AbstractBackend implements Backend {
    String parentId

    String id
    String ext

    protected Map<Node.Type, NodeRenderer> renderers = [:]

    ChunkRenderer chunkRenderer

    /**
     * No default chunk path
     */
    @Override
    public String getChunkPath(DocumentContext context) {
        return null
    }

    NodeRenderer getRenderer(Node node) {
        def renderer = null

        def type = node.type
        def subtype = (node in Subtype) ? node.subtype : null

        while (renderer == null && type != null) {
            renderer = findRenderer(type, subtype)
            type = type.parent
        }

        if (renderer == null) {
            def parentBackend = PluginRegistry.instance.getBackend(parentId)
            if (parentBackend != null) {
                renderer = parentBackend.getRenderer(node)
            }
        }
        return renderer
    }

    LeafNodeRenderer getInlineRenderer(Node node) {
        def nodeType = node.type

        if (!nodeType.isInline()) {
            throw new IllegalArgumentException("Node type \"${nodeType}\" is not an inline type")
        }

        def renderer = getRenderer(node)
        if (!(renderer in LeafNodeRenderer)) {
            renderer = null
        }

        return renderer
    }

    @Override

    void registerRenderer(NodeRenderer renderer) {
        def nodeType = renderer.nodeType
        def subtype = null
        if (renderer in Subtype) {
            subtype = renderer.subtype
        }

        def submap = renderers[nodeType]
        if (submap == null) {
            submap = [:]
            renderers[nodeType] = submap
        }
        submap[subtype] = renderer
    }

    void startRendering(DocumentContext context) {
        doStartRendering(context)
    }

    void endRendering(DocumentContext context) {
        doEndRendering(context)
    }

    void doStartRendering(DocumentContext context) {
    }

    void doEndRendering(DocumentContext context) {
    }

    protected NodeRenderer findRenderer(Node node) {
        def subtype = null
        if (node in Subtype) {
            subtype = node.subtype
        }

        return findRenderer(node.type, subtype)
    }

    protected NodeRenderer findRenderer(Node.Type type, String subtype) {
        def submap = renderers[type]
        if (submap == null) {
            return null
        }

        return submap[subtype]
    }

}
