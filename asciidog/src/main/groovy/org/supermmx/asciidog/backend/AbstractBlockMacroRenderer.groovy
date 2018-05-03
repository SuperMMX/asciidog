package org.supermmx.asciidog.backend

import org.supermmx.asciidog.Subtype
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.backend.AbstractLeafNodeRenderer
import org.supermmx.asciidog.converter.DocumentContext

abstract class AbstractBlockMacroRenderer extends AbstractLeafNodeRenderer implements Subtype {
    String name

    AbstractBlockMacroRenderer() {
        nodeType = Node.Type.BLOCK_MACRO
    }

    @Override
    String getSubtype() {
        return name
    }

    @Override
    boolean accept(Node node) {
        return super.accept(node) && name == node.subtype
    }

    @Override
    void doPre(DocumentContext context, Node node) {
    }

    @Override
    void doPost(DocumentContext context, Node node) {
    }
}
