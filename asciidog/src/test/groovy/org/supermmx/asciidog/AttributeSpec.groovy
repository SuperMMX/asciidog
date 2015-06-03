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

    def 'render properly with simple names'() {
        given:
        def paraContent = '''Yo, {frog}!
Beat {my_super-hero}!'''
        def content = """= Document Title
:frog: Tanglefoot
:my_super-hero: Spiderman

${paraContent}
"""
        def paragraph = builder.paragraph() {
            current.info = inlineInfo(constrained: false, escaped: false,
                                      start: 0, end: paraContent.length(), contentStart:0, contentEnd: paraContent.length())
            current.inlineNodes = [
                new TextNode('Yo, ', 0),
                attributeReferenceNode(type: Node.Type.INLINE_ATTRIBUTE_REFERENCE,
                                       name: 'frog') {
                    current.info = inlineInfo(constrained: false, escaped: false,
                                              start: 4, end: 10, contentStart:5, contentEnd: 9)
                },
                new TextNode('!\nBeat ', 10),
                attributeReferenceNode(type: Node.Type.INLINE_ATTRIBUTE_REFERENCE,
                                       name: 'my_super-hero') {
                    current.info = inlineInfo(constrained: false, escaped: false,
                                              start: 17, end: 32, contentStart:18, contentEnd: 31)
                },
                new TextNode('!', 32)
            ]
        }

        when:
        def parser = parser(content)
        def doc = parser.parseDocument()

        then:
        doc.preamble.blocks[0] == paragraph
    }
}
