package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.TextNode

import groovy.util.logging.Slf4j

@Slf4j
class AttributeFactory extends InlineContainerFactory {
    AttributeFactory() {
        name = 'attribute'
    }

    @Override
    def newInstance(FactoryBuilderSupport builder, nodeName, args, Map attributes) {
        AttributeEntry attr = new AttributeEntry()

        if (args?.size() >= 1) {
            attr.name = args[0]
        }
        if (args?.size() >= 2) {
            attr << new TextNode(args[1])
        }

        return attr
    }
}
