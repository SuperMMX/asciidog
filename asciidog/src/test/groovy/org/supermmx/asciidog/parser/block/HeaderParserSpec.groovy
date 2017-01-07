package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.action.AttributeEntryParser

class HeaderParserSpec extends AsciidogSpec {
    def parser = new HeaderParser()

    def 'checkStart'() {
        expect:
        isStart == parser.checkStart('', null, expected)

        where:
        isStart | expected
        true    | true
        false   | false
    }

    def 'nextChildParser: with author'() {
        given:
        def context = parserContext('')
        context.block = new Block()

        expect:
        parser.getNextChildParser(context) == AuthorParser.ID
        parser.getNextChildParser(context) == AttributeEntryParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'nextChildParser: with author and attribute'() {
        given:
        def context = parserContext('')
        context.block = new Block()

        expect:
        parser.getNextChildParser(context) == AuthorParser.ID
        parser.getNextChildParser(context) == AttributeEntryParser.ID

        when:
        context.block << new Node()

        then:
        parser.getNextChildParser(context) == AttributeEntryParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'standalone: with author'() {
        given:
        def content = '''John Doe <john.doe@email.com>

paragraph
'''
        def expectedHeader = builder.header {
            authors {
                author 'John Doe <john.doe@email.com>'
            }
        }
        def context = parserContext(content)
        context.parserId = HeaderParser.ID
        context.expected = true

        when:
        def header = Parser.parse(context)

        then:
        header == expectedHeader
    }

    def 'standalone: with one attribute'() {
        given:
        def content = ''':name: value

paragraph
'''
        def expectedHeader = builder.header {
            attribute 'name', 'value'
        }
        def context = parserContext(content)
        context.parserId = HeaderParser.ID
        context.expected = true

        when:
        def header = Parser.parse(context)

        then:
        header == expectedHeader
    }

    def 'standalone: with more attributes'() {
        given:
        def content = ''':name: value
:name2: value2
:name3: value3

paragraph
'''
        def expectedHeader = builder.header {
            attribute 'name', 'value'
            attribute 'name2', 'value2'
            attribute 'name3', 'value3'
        }
        def context = parserContext(content)
        context.parserId = HeaderParser.ID
        context.expected = true

        when:
        def header = Parser.parse(context)

        then:
        header == expectedHeader
    }

    def 'standalone: with author and one attribute'() {
        given:
        def content = '''John Doe <john.doe@email.com>
:name: value

paragraph
'''
        def expectedHeader = builder.header {
            authors {
                author 'John Doe <john.doe@email.com>'
            }
            attribute 'name', 'value'
        }
        def context = parserContext(content)
        context.parserId = HeaderParser.ID
        context.expected = true

        when:
        def header = Parser.parse(context)

        then:
        header == expectedHeader
    }

    def 'standalone: with author and more attributes'() {
        given:
        def content = '''John Doe <john.doe@email.com>
:name: value
:name2: value2
:name3: value3

paragraph
'''
        def expectedHeader = builder.header {
            authors {
                author 'John Doe <john.doe@email.com>'
            }
            attribute 'name', 'value'
            attribute 'name2', 'value2'
            attribute 'name3', 'value3'
        }
        def context = parserContext(content)
        context.parserId = HeaderParser.ID
        context.expected = true

        when:
        def header = Parser.parse(context)

        then:
        header == expectedHeader
    }
}
