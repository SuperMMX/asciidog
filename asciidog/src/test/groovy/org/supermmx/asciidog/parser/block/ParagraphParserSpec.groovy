package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.ast.Block

class ParagraphParserPluginSpec extends AsciidogSpec{
    def 'is start of a paragraph'() {
        given:
        def parser = new ParagraphParser()
        def header = new BlockParserPlugin.BlockHeader()

        expect:
        parser.isStart(line, header) == value

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
        def parser = new ParagraphParser()
        def header = new BlockParserPlugin.BlockHeader()

        def context = parserContext(content)

        def parent = new Block()
        def parentParser = Mock(BlockParserPlugin)

        context.blockHeader = header
        context.parents.push(parent)
        context.parentParsers.push(parentParser)

        parentParser.toEndParagraph(_, _) >> false

        when:
        def para = parser.parse(context)

        then:
        para.lines == [ 'first line', 'second line' ]
    }
}
