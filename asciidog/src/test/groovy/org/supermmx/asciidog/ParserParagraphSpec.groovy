package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserParagraphSpec extends AsciidogSpec {
    def 'parse: paragraph: single line'() {
        given:
        def content = 'paragraph'

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            text 'paragraph'
        }
    }

    def 'parse: paragraph: single line starting with leading blank lines'() {
        given:
        def text = 'paragraph'
        def content = """


$text"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }

    def 'parse: paragraph: multiple lines starting with leading blank lines'() {
        given:
        def text = '''line1
line2
line3'''
        def content = """


$text"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }

    def 'parse: paragraph: no new line in the end'() {
        given:
        def text = '''line1
line2
line3'''
        def content = """
$text"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }

    def 'parse: paragraph: new line in the end'() {
        given:
        def text = '''line1
line2
line3'''
        def content = """
$text
"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }

    def 'parse: paragraph: blank line after'() {
        given:
        def text = '''line1
line2
line3'''
        def content = """
$text

"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }

    def 'parse: paragraph: another paragraph after'() {
        given:
        def text = '''line1
line2
line3'''
        def content = """
$text

abc
"""

        expect:
        parser(content).parseParagraph(new Block()) == builder.para {
            builder.text text
        }
    }
}
