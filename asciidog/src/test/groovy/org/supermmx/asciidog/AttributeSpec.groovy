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
        header == builder.header('Document Title') {
            attribute('doctype', 'book')
            attribute('var', 'initial')
            attribute('var', 'new')
        }

        parser.attrContainer.getAttribute('var') == new Attribute([ name: 'var',
                                                                    type: Attribute.ValueType.INLINES,
                                                                    value: [ new TextNode('new')] ])
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
            new TextNode('Asciidog '),
            new TextNode('1.0')
        ]

        when:
        def parser = parser(content)
        def header = parser.parseHeader()

        then:
        header == builder.header('Document Title') {
            attribute('version', '1.0')
            attribute('release', 'Asciidog {version}')
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
        def paragraph = builder.para {
            text 'Yo, '
            aref 'frog'
            text '!\nBeat '
            aref 'my_super-hero'
            text '!'
        }

        when:
        def parser = parser(content)
        def doc = parser.parseDocument()

        then:
        doc.preamble.blocks[0] == paragraph
    }
}
