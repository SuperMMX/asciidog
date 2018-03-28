package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

class ParagraphParserSpec extends AsciidogSpec {
    def parser = new ParagraphParser()

    def 'checkStart: check start of a paragraph'() {
        given:

        expect:
        parser.checkStart(parserContext(line), null, true) == value
        parser.checkStart(parserContext(line), null, false) == value

        where:
        line      | value
        'abc'     | true
        '  abc	' | true
        ''        | false
        '  	'     | false
        null      | false
    }

    def 'standalone: normal paragraph'() {
        given:
        def content = '''first line
second line'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text content
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'standalone: end paragraph parsing'() {
        given:
        def content = '''first line
second line
--
fourth line'''
        def context = parserContext(content)
        context.parserId = parser.id

        def parentParser = Spy(BlockParserPlugin)

        parentParser.id >> 'parent'
        parentParser.toEndParagraph(_) >> { args -> args[0].lexer.peek().value == '--' }

        PluginRegistry.instance.register(parentParser)
        context.parentParserId = 'parent'
        context.paragraphEndingCheckers << parentParser

        def ePara = builder.para {
            text '''first line
second line'''
        }

        expect:
        parentParser.id == 'parent'

        when:
        def para = parser.parse(context)
        PluginRegistry.instance.unregister('parent')

        then:
        para == ePara
    }

    def 'standalone: blank lines before and after'() {
        given:
        def content = '''

first line
second line
third line

'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text '''first line
second line
third line'''
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }
}
