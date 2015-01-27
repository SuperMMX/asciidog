package org.supermmx.asciidog

import static org.supermmx.asciidog.ast.Document.DOCTYPE

import org.supermmx.asciidog.ast.Document

import spock.lang.*

class AttributeContainerSpec extends Specification {
    def 'default 0, attribute 0, system 0'() {
        given:
        def attrCon = new AttributeContainer()

        expect:
        attrCon.systemAttributes['new'] == null
        attrCon.attributes['new'] == null
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == null
    }

    def 'default 0, attribute 0, system 1'() {
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
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == attr
    }

    def 'default 0, attribute 1, system 0'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setAttribute('new', 'new-value')

        then:

        attr.name == 'new'
        attr.type == Attribute.ValueType.STRING
        attr.value == 'new-value'

        attrCon.systemAttributes['new'] == null
        attrCon.attributes['new'] == attr
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == attr
    }

    def 'default 0, attribute 1, system 1'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setAttribute('new', 'attribute')
        def sysAttr = attrCon.setSystemAttribute('new', 'system')

        then:

        attr.name == 'new'
        attr.type == Attribute.ValueType.STRING
        attr.value == 'attribute'

        sysAttr.name == 'new'
        sysAttr.type == Attribute.ValueType.STRING
        sysAttr.value == 'system'

        attrCon.systemAttributes['new'] == sysAttr
        attrCon.attributes['new'] == attr
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == sysAttr
    }

    def 'default 1, attribute 0, system 0'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.getAttribute(DOCTYPE)

        then:

        attr.name == DOCTYPE
        attr.type == Attribute.ValueType.STRING
        attr.value == 'article'

        attrCon.systemAttributes[DOCTYPE] == null
        attrCon.attributes[DOCTYPE] == null
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE] == attr
    }

    def 'default 1, attribute 1, system 0'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setAttribute(DOCTYPE, 'book')

        then:

        attr.name == DOCTYPE
        attr.type == Attribute.ValueType.STRING
        attr.value == 'book'

        attrCon.systemAttributes[DOCTYPE] == null
        attrCon.attributes[DOCTYPE] == attr
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.Type.article.toString()

        // getAttribute
        attrCon.getAttribute(DOCTYPE) == attr
    }

    def 'default 1, attribute 0, system 1'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def attr = attrCon.setSystemAttribute(DOCTYPE, 'book')

        then:

        attr.name == DOCTYPE
        attr.type == Attribute.ValueType.STRING
        attr.value == 'book'

        attrCon.systemAttributes[DOCTYPE] == attr
        attrCon.attributes[DOCTYPE] == null
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.Type.article.toString()

        attrCon.getAttribute(DOCTYPE) == attr
    }

    def 'default 1, attribute 1, system 1'() {
        given:
        def attrCon = new AttributeContainer()

        when:
        def sysAttr = attrCon.setSystemAttribute(DOCTYPE, 'book')
        def attr = attrCon.setAttribute(DOCTYPE, 'inline')

        then:

        sysAttr.name == DOCTYPE
        sysAttr.type == Attribute.ValueType.STRING
        sysAttr.value == 'book'

        attr == sysAttr

        attrCon.systemAttributes[DOCTYPE] == sysAttr
        attrCon.attributes[DOCTYPE] == new Attribute([ name: DOCTYPE,
                                                       type: Attribute.ValueType.STRING,
                                                       value: 'inline' ])
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.Type.article.toString()

        attrCon.getAttribute(DOCTYPE) == sysAttr
    }
}
