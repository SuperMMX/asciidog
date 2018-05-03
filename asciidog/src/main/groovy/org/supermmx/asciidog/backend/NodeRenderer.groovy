package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * A renderer renders the document
 */
interface NodeRenderer {
    /**
     * Get the id of the backend that the renderer is for
     */
    String getBackendId()

    /**
     * Get the node type that this node renderer renders
     */
    Node.Type getNodeType()

    /**
     * Whether this renderer can render this node
     */
    boolean accept(Node node)

    /**
     * Before start rendering the node
     */
    void pre(DocumentContext context, Node node)

    /**
     * After rending the node
     */
    void post(DocumentContext context, Node node)
}
