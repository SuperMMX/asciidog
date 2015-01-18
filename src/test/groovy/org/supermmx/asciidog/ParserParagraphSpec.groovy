package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserParagraphSpec extends Specification {
    def 'parse: paragraph: single line'() {
        given:
        def content = 'paragraph'

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'paragraph' ]
    }

    def 'parse: paragraph: single line starting with leading blank lines'() {
        given:
        def content = '''


paragraph'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'paragraph' ]
    }

    def 'parse: paragraph: multiple lines starting with leading blank lines'() {
        given:
        def content = '''


line1
line2
line3'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'line1', 'line2', 'line3' ]
    }

    def 'parse: paragraph: no new line in the end'() {
        given:
        def content = '''
line1
line2
line3'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'line1', 'line2', 'line3' ]
    }

    def 'parse: paragraph: new line in the end'() {
        given:
        def content = '''
line1
line2
line3
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'line1', 'line2', 'line3' ]
    }

    def 'parse: paragraph: blank line after'() {
        given:
        def content = '''
line1
line2
line3

'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'line1', 'line2', 'line3' ]
    }

    def 'parse: paragraph: another paragraph after'() {
        given:
        def content = '''
line1
line2
line3

abc
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para != null
        para.lines == [ 'line1', 'line2', 'line3' ]
    }
}
