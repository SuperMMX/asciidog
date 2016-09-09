package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.parser.ParserContext

class ParagraphParserPluginSpec extends AsciidogSpec{
    def parser
    def context

    def setup() {
        parser = new ParagraphParser()
        context = new ParserContext()

        def parent = new Block()
        def parentParser = Mock(BlockParserPlugin)

        context.blockHeader = new BlockParserPlugin.BlockHeader()
        context.parents.push(parent)
        context.parentParsers.push(parentParser)

    }

    def 'is start of a paragraph'() {
        expect:
        parser.isStart(line, context.blockHeader) == value

        where:
        line      | value
        'abc'     | true
        '  abc	' | true
        ''        | false
        '  	'     | false
        null      | false
    }

    def 'normal paragraph'() {
        given:
        def content = '''first line
second line'''
        context.reader = Reader.createFromString(content)

        context.parentParser.toEndParagraph(_, _) >> false

        when:
        def para = parser.parse(context)

        then:
        para.lines == [ 'first line', 'second line' ]
    }

    def 'end paragraph parsing'() {
        given:
        def content = '''first line
second line
--
fourth line'''
        context.reader = Reader.createFromString(content)

        context.parentParser.toEndParagraph(_, '--') >> true
        context.parentParser.toEndParagraph(_, _) >> false

        when:
        def para = parser.parse(context)

        then:
        para.lines == [ 'first line', 'second line' ]
    }

    def 'blank lines before and after'() {
        given:
        def content = '''

first line
second line
third line

'''
        context.reader = Reader.createFromString(content)

        context.parentParser.toEndParagraph(_, _) >> false

        when:
        def para = parser.parse(context)

        then:
        para.lines == [ 'first line', 'second line', 'third line' ]
    }
}
