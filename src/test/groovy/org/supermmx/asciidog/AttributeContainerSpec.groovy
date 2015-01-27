package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document

import spock.lang.*

class AttributeContainerSpec extends Specification {
    def 'set system attribute on non-default attribute'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setSystemAttribute('new', 'new-value')

        then:

        attr.name == 'new'
        attr.type == Attribute.ValueType.STRING
        attr.value == 'new-value'

        attrCon.systemAttributes['new'] == attr
        attrCon.attributes['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == attr
    }

    def 'set system attribute on default attribute'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setSystemAttribute(Document.DOCTYPE, 'book')

        then:

        attr.name == Document.DOCTYPE
        attr.type == Attribute.ValueType.STRING
        attr.value == 'book'

        attrCon.systemAttributes[Document.DOCTYPE] == attr
        attrCon.attributes[Document.DOCTYPE] == null
        attrCon.DEFAULT_ATTRIBUTES[Document.DOCTYPE].value == Document.Type.article.toString()

        attrCon.getAttribute(Document.DOCTYPE) == attr
    }
}
