package org.supermmx.asciidog

import static org.supermmx.asciidog.ast.Document.DOCTYPE

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.TextNode

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
        def expectedAttr = new Attribute(name: 'new', type: Attribute.ValueType.STRING,
                                         value: 'new-value',
                                         valueString: 'new-value')

        when:
        def attr = attrCon.setSystemAttribute('new', Attribute.ValueType.STRING, 'new-value')

        then:

        attr == expectedAttr

        attrCon.systemAttributes['new'] == attr
        attrCon.attributes['new'] == null
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == expectedAttr
    }

    def 'default 0, attribute 1, system 0'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: 'new', type: Attribute.ValueType.STRING,
                                         value: 'new-value',
                                         valueString: 'new-value')

        when:
        def attr = attrCon.setAttribute('new', Attribute.ValueType.STRING, 'new-value')

        then:

        attr == expectedAttr

        attrCon.systemAttributes['new'] == null
        attrCon.attributes['new'] == attr
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == expectedAttr
    }

    def 'default 0, attribute 1, system 1'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: 'new', type: Attribute.ValueType.STRING,
                                         value: 'attribute',
                                         valueString: 'attribute')
        def expectedSysAttr = new Attribute(name: 'new', type: Attribute.ValueType.STRING,
                                            value: 'system',
                                            valueString: 'system')

        when:
        def attr = attrCon.setAttribute('new', Attribute.ValueType.STRING, 'attribute')
        def sysAttr = attrCon.setSystemAttribute('new', Attribute.ValueType.STRING, 'system')

        then:

        attr == expectedAttr
        sysAttr == expectedSysAttr

        attrCon.systemAttributes['new'] == sysAttr
        attrCon.attributes['new'] == attr
        attrCon.DEFAULT_ATTRIBUTES['new'] == null

        // getAttribute
        attrCon.getAttribute('new') == sysAttr
    }

    def 'default 1, attribute 0, system 0'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: DOCTYPE, type: Attribute.ValueType.STRING,
                                         value: 'article',
                                         valueString: 'article')

        when:
        def attr = attrCon.getAttribute(DOCTYPE)

        then:

        attr == expectedAttr

        attrCon.systemAttributes[DOCTYPE] == null
        attrCon.attributes[DOCTYPE] == null
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE] == attr
    }

    def 'default 1, attribute 1, system 0'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: DOCTYPE, type: Attribute.ValueType.STRING,
                                         value: 'book',
                                         valueString: 'book')

        when:
        def attr = attrCon.setAttribute(DOCTYPE, 'book')

        then:

        attr == expectedAttr

        attrCon.systemAttributes[DOCTYPE] == null
        attrCon.attributes[DOCTYPE] == attr
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.DocType.article.toString()

        // getAttribute
        attrCon.getAttribute(DOCTYPE) == attr
    }

    def 'default 1, attribute 0, system 1'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: DOCTYPE, type: Attribute.ValueType.STRING,
                                         value: 'book',
                                         valueString: 'book')

        when:
        def attr = attrCon.setSystemAttribute(DOCTYPE, 'book')

        then:

        attr == expectedAttr

        attrCon.systemAttributes[DOCTYPE] == attr
        attrCon.attributes[DOCTYPE] == null
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.DocType.article.toString()

        attrCon.getAttribute(DOCTYPE) == attr
    }

    def 'default 1, attribute 1, system 1'() {
        given:
        def attrCon = new AttributeContainer()
        def expectedAttr = new Attribute(name: DOCTYPE, type: Attribute.ValueType.STRING,
                                         value: 'book',
                                         valueString: 'book')

        when:
        def sysAttr = attrCon.setSystemAttribute(DOCTYPE, 'book')
        def attr = attrCon.setAttribute(DOCTYPE, 'inline')

        then:

        sysAttr == expectedAttr
        attr == sysAttr

        attrCon.systemAttributes[DOCTYPE] == sysAttr
        attrCon.attributes[DOCTYPE] == new Attribute([ name: DOCTYPE,
                                                       type: Attribute.ValueType.STRING,
                                                       value: 'inline',
                                                       valueString: 'inline'])
        attrCon.DEFAULT_ATTRIBUTES[DOCTYPE].value == Document.DocType.article.toString()

        attrCon.getAttribute(DOCTYPE) == sysAttr
    }


    def 'replace single attribute reference'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('init', 'initial value')
        container.setAttribute('newValue', 'new {init}')
        container.setAttribute('newNewValue', 'new {newValue}')

        then:
        container.getAttribute('newValue') == new Attribute(name: 'newValue',
                                                            type: Attribute.ValueType.INLINES,
                                                            value: [
                                                                new TextNode('new '),
                                                                new TextNode('initial value')
                                                            ],
                                                            valueString: 'new {init}')
        container.getAttribute('newNewValue') == new Attribute(name: 'newNewValue',
                                                            type: Attribute.ValueType.INLINES,
                                                            value: [
                                                                new TextNode('new '),
                                                                new TextNode('new '),
                                                                new TextNode('initial value')
                                                            ],
                                                               valueString: 'new {newValue}')
    }

    def 'should delete an attribute that ends with !'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog', Attribute.ValueType.STRING, 'Tanglefoot')
        container.setAttribute('frog!', null)

        then:
        container.getAttribute('frog') == null
    }

    def 'should delete an attribute that ends with ! set via API'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog', Attribute.ValueType.STRING, 'Tanglefoot')
        container.setSystemAttribute('frog!', '')

        then:
        container.getAttribute('frog') == null
    }

    def 'should delete an attribute that starts with !'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog', Attribute.ValueType.STRING, 'Tanglefoot')
        container.setAttribute('!frog', null)

        then:
        container.getAttribute('frog') == null
    }

    def 'should delete an attribute that starts with ! set via API'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog', Attribute.ValueType.STRING, 'Tanglefoot')
        container.setSystemAttribute('!frog', '')

        then:
        container.getAttribute('frog') == null
    }

    def 'should delete an attribute set via API to nil value'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog', Attribute.ValueType.STRING, 'Tanglefoot')
        container.setSystemAttribute('frog', null)

        then:
        container.getAttribute('frog') == null
    }

    def 'doesn\'t choke when deleting a non-existing attribute'() {
        given:
        def container = new AttributeContainer()

        when:
        container.setAttribute('frog!', Attribute.ValueType.STRING, '')

        then:
        container.getAttribute('frog') == null
    }

    def 'performs attribute substitution on attribute value'() {
        given:
        def attr = new Attribute(name: 'release',
                                 type: Attribute.ValueType.INLINES,
                                 value: [
                                     new TextNode('Asciidoctor '),
                                     new TextNode('1.0')
                                 ],
                                 valueString: 'Asciidoctor {version}')

        def container = new AttributeContainer()

        when:
        container.setAttribute('version', '1.0')
        container.setAttribute('release', 'Asciidoctor {version}')

        then:
        container.getAttribute('release') == attr
    }

    def 'Raw values'() {
        given:
        def date = new Date()
        def container = new AttributeContainer()
        def boolAttr = new Attribute(name: 'boolAttr',
                                     type: Attribute.ValueType.BOOLEAN,
                                     value: true)
        def intAttr = new Attribute(name: 'intAttr',
                                    type: Attribute.ValueType.INTEGER,
                                    value: 25)
        def decimalAttr = new Attribute(name: 'decimalAttr',
                                        type: Attribute.ValueType.DECIMAL,
                                        value: 25.5)
        def objAttr = new Attribute(name: 'objAttr',
                                    type: Attribute.ValueType.OBJECT,
                                    value: date)

        when:
        container.setAttribute('boolAttr', true)
        container.setAttribute('intAttr', 25)
        container.setAttribute('decimalAttr', 25.5)
        container.setAttribute('objAttr', date)

        then:
        container.getAttribute('boolAttr') == boolAttr
        container.getAttribute('intAttr') == intAttr
        container.getAttribute('decimalAttr') == decimalAttr
        container.getAttribute('objAttr') == objAttr
    }
}
