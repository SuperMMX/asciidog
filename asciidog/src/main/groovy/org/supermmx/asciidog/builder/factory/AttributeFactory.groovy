package org.supermmx.asciidog.builder.factory

import org.supermmx.asciidog.ast.AttributeEntry

import groovy.util.logging.Slf4j

@Slf4j
class AttributeFactory extends AbstractBlockFactory {
    @Override
    def newInstance(FactoryBuilderSupport builder, nodeName, nodeValue, Map attributes) {
        log.info "name = ${nodeName}, value = ${nodeValue}, attribute = ${attributes}"
        def name = null
        def value = null
        if (nodeValue instanceof List) {
            if (nodeValue.size() > 0) {
                name = nodeValue[0]
            }
            if (nodeValue.size() > 1) {
                value = nodeValue[1]
            }
        } else {
            name = nodeValue
        }
        AttributeEntry attr = new AttributeEntry(name: name, value: value)

        return attr
    }
}
