package org.supermmx.asciidog

import org.supermmx.asciidog.Parser.BlockHeader

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
        header.properties[BlockHeader.SECTION_TITLE] == 'section'
        header.properties[BlockHeader.SECTION_LEVEL] == 1
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
}
