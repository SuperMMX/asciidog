package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * Null node renderer that accept any node and render nothing
 */
class NullNodeRenderer implements LeafNodeRenderer {
    String getBackendId() {
        return null
    }

    Node.Type getNodeType() {
        return null
    }

    boolean accept(Node node) {
        return true
    }

    void pre(DocumentContext context, Node node) {
    }

    void render(DocumentContext context, Node node) {
    }

    void post(DocumentContext context, Node node) {
    }
}
