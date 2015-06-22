package org.supermmx.asciidog.backend

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.converter.DocumentContext

/**
 * Abstract nod renderer
 */
abstract class AbstractNodeRenderer implements NodeRenderer {
    Node.Type nodeType

    boolean accept(Node node) {
        return nodeType == node.type
    }

    void pre(DocumentContext context, Node node) {
        if (accept(node)) {
            doPre(context, node)
        }
    }

    void post(DocumentContext context, Node node) {
        if (accept(node)) {
            doPost(context, node)
        }
    }

    abstract void doPre(DocumentContext context, Node node)

    abstract void doPost(DocumentContext context, Node node)
}
