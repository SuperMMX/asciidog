package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.FormattingNode

abstract class InlineContainerFactory extends AbstractFactory {
    boolean isLeaf() {
        false
    }

    void setChild(FactoryBuilderSupport builder, parent, child) {
        if (
        parent.inlineNodes << child
    }
}
