package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.CommentLine
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserBlocksSpec extends AsciidogSpec {
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
        blocks[0] == para(content)
    }

    def 'parse: blocks: multiple blocks leading blank lines'() {
        given:
        def text1 = '''new paragraph
with another line'''
        def text2 = '''second paragraph'''
        def content = """

$text1

$text2
"""

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def paras = parser.parseBlocks(new Block())

        then:
        paras == [ para(text1), para(text2) ]
    }

    def 'parse: blocks: multiple blocks with section after'() {
        given:
        def text1 = '''new paragraph
with another line'''
        def text2 = '''second paragraph'''
        def content = """

$text1

$text2

== Section Title

"""

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def paras = parser.parseBlocks(new Block())

        then:
        paras == [ para(text1), para(text2) ]
    }

    def 'blocks with comment line in between'() {
        given:
        def text1 = '''new paragraph
with another line'''
        def text2 = '''second paragraph'''
        def content = """

$text1

// comment line 

$text2
"""

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def blocks = parser.parseBlocks(new Block())

        then:
        blocks == [ para(text1), new CommentLine(lines: [' comment line ']), para(text2) ]
    }
}
