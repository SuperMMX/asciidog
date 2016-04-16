package org.supermmx.asciidog

import org.supermmx.asciidog.Parser.BlockHeader

import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserBlockHeaderSpec extends Specification {
    def 'section'() {
        given:
        def content =
'''
== section
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.SECTION
        header.properties.size() == 2
        header.properties[BlockHeader.SECTION_TITLE] == 'section'
        header.properties[BlockHeader.SECTION_LEVEL] == 1

        header.id == null
        header.attributes.size() == 0
    }

    def 'section with id'() {
        given:
        def content =
'''
[[sec-id]]
== section
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.SECTION
        header.properties[BlockHeader.SECTION_TITLE] == 'section'
        header.properties[BlockHeader.SECTION_LEVEL] == 1
        header.id == 'sec-id'
    }

    def 'section with id and non-sense block title'() {
        given:
        def content =
'''
[[sec-id]]
.non-sense-title
== section
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.SECTION
        header.properties[BlockHeader.SECTION_TITLE] == 'section'
        header.properties[BlockHeader.SECTION_LEVEL] == 1
        header.id == 'sec-id'

        header.title == 'non-sense-title'
    }

    def 'section with style'() {
        given:
        def content =
'''
[preface]
== section
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.SECTION
        header.properties.size() == 2
        header.properties[BlockHeader.SECTION_TITLE] == 'section'
        header.properties[BlockHeader.SECTION_LEVEL] == 1
        header.attributes == [ 'preface': null ]

        header.id == null
    }

    def 'paragraph'() {
        given:
        def content =
'''
this is a paragraph
test of multiple lines
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.PARAGRAPH
        header.id == null
        header.title == null
        header.attributes.size() == 0
        header.properties.size() == 0
    }

    def 'paragraph with id and title'() {
        given:
        def content =
'''
.title
[[id]]
this is a paragraph
test of multiple lines
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.PARAGRAPH
        header.id == 'id'
        header.title == 'title'
        header.attributes.size() == 0
        header.properties.size() == 0
    }

    def 'paragraph with id, attributes and title'() {
        given:
        def content =
'''

[[id]]
.title
[NOTE, options=interactive]
this is a paragraph
test of multiple lines
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.PARAGRAPH
        header.id == 'id'
        header.title == 'title'
        header.attributes == ['NOTE': null, 'options': 'interactive']
        header.attributes.keySet() as String[] == ['NOTE', 'options']

        header.properties.size() == 0
    }

    def 'simple comment line'() {
        given:
        def content =
'''
// comment line 
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.COMMENT_LINE

        header.properties.size() == 1
        header.properties[BlockHeader.COMMENT_LINE_COMMENT] == ' comment line '
    }

    def 'only block headers'() {
        given:
        def content =
'''
:name: value
[[id]]
[attribute]
.title
:name2: value2
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def header = parser.parseBlockHeader()

        then:
        header.type == Node.Type.DEFINE_ATTRIBUTE

        header.id == 'id'
        header.title == 'title'
        header.attributes == ['attribute': null]
        header.actionBlocks == [
            new AttributeEntry([name: 'name', value: 'value']),
            new AttributeEntry([name: 'name2', value: 'value2'])
        ]
    }
}
