package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.LinkNode

class LinkFactory extends InlineContainerFactory {
    LinkFactory() {
        name = 'link'
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        LinkNode link = new LinkNode(target: value)

        return link
    }
}
