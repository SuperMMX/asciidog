package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserBlocksSpec extends Specification {
    def 'parse: blocks: single block'() {
        given:
        def content = 'paragraph'

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def blocks = parser.parseBlocks(new Block())

        then:
        blocks.size() == 1
        blocks[0].lines == [ 'paragraph' ]
    }

    def 'parse: blocks: multiple blocks leading blank lines'() {
        given:
        def content = '''

new paragraph
with another line

second paragraph

'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def lines = parser.parseBlocks(new Block())

        then:
        lines.size() == 2
        lines[0].lines == [ 'new paragraph', 'with another line' ]
        lines[1].lines == [ 'second paragraph' ]
    }

    def 'parse: blocks: multiple blocks with section after '() {
        given:
        def content = '''

new paragraph
with another line

second paragraph

== Section Title

'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def lines = parser.parseBlocks(new Block())

        then:
        lines.size() == 2
        lines[0].lines == [ 'new paragraph', 'with another line' ]
        lines[1].lines == [ 'second paragraph' ]
    }

    def 'blocks with comment line in between'() {
        given:
        def content = '''
new paragraph
with another line

// comment line 

second paragraph

'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def blocks = parser.parseBlocks(new Block())

        then:
        blocks.size() == 3
        blocks[0].lines == [ 'new paragraph', 'with another line' ]
        blocks[1].lines == [ ' comment line ' ]
        blocks[2].lines == [ 'second paragraph' ]
    }
}
