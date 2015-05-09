package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode

import spock.lang.*

class AttributeSpec extends AsciidogSpec {
    def 'parse attributes'() {
        given:
        def content = '''= Document Title
:doctype: book
:var: initial
:var: new
'''
        when:
        def parser = parser(content)
        def header = parser.parseHeader()

        then:
        header == builder.header(title: 'Document Title') {
            current.blocks = [
                attributeEntry(name: 'doctype', value: 'book'),
                attributeEntry(name: 'var', value: 'initial'),
                attributeEntry(name: 'var', value: 'new'),
            ]
        }

        parser.attrContainer.getAttribute('var') == new Attribute([ name: 'var',
                                                                    type: Attribute.ValueType.INLINES,
                                                                    value: [ new TextNode('new', 0)] ])
    }

    def 'performs attribute substitution on attribute value'() {
        given:
        def content = '''= Document Title
:version: 1.0
:release: Asciidog {version}
'''
        def releaseAttr = new Attribute(name: 'release',
                                        type: Attribute.ValueType.INLINES)
        releaseAttr.value = [
            new TextNode('Asciidog ', 0),
            new TextNode('1.0', 0)
        ]

        when:
        def parser = parser(content)
        def header = parser.parseHeader()

        then:
        header == builder.header(title: 'Document Title') {
            current.blocks = [
                attributeEntry(name: 'version', value: '1.0'),
                attributeEntry(name: 'release', value: 'Asciidog {version}')
            ]
        }

        parser.attrContainer.getAttribute('release') == releaseAttr
    }
}
