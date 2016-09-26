package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Node

class PreambleParserSpec extends AsciidogSpec {
    def preambleParser = new SectionParser()

    def 'standalone: no block header'() {
        given:
        def content = '== Section Title'

        def context = parserContext(content)
        context.parserId = PreambleParser.ID

        when:
        def preamble = Parser.parse(context)

        then:
        preamble == null
    }

    def 'standalone: no preamble blocks'() {
        given:
        def content = '== Section Title'

        def context = parserContext(content)
        context.parserId = PreambleParser.ID
        context.blockHeader = new BlockParserPlugin.BlockHeader(type: Node.Type.SECTION)

        when:
        def preamble = Parser.parse(context)

        then:
        preamble == null
    }
}
