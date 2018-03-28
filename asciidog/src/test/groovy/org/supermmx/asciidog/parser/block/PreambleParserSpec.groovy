package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Node

class PreambleParserSpec extends AsciidogSpec {
    def parser = new PreambleParser()

    def 'checkStart: check start'() {
        expect:
        parser.checkStart(parserContext(line), header, expected) == value

        where:
        value | expected | line | header
        false | false    | null | null
        false | true     | null | null
        false | true     | null | new BlockParserPlugin.BlockHeader()
        false | true     | null | new BlockParserPlugin.BlockHeader(type: Node.Type.SECTION)
        true  | true     | null | new BlockParserPlugin.BlockHeader(type: Node.Type.PARAGRAPH)
    }

    def 'standalone: no block header'() {
        given:
        def content = '== Section Title'

        def context = parserContext(content)
        context.parserId = parser.id
        context.expected = true

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
        context.expected = true
        context.blockHeader = new BlockParserPlugin.BlockHeader(type: Node.Type.SECTION)

        when:
        def preamble = Parser.parse(context)

        then:
        preamble == null
    }

    def 'standalone: paragraphs'() {
        given:
        def content = '''line1
line2

line3
line4
line5

'''

        def context = parserContext(content)
        context.parserId = PreambleParser.ID
        context.expected = true
        context.blockHeader = new BlockParserPlugin.BlockHeader(type: Node.Type.PARAGRAPH,
                                                                parserId: ParagraphParser.ID)

        when:
        def preamble = Parser.parse(context)

        then:
        preamble == builder.preamble {
            para {
                text '''line1
line2'''
            }
            para {
                text '''line3
line4
line5'''
            }
        }
    }

    def 'document: with only preamble'() {
        given:
        def content = '''= Document Title

line1
line2

line3
line4
line5

'''
        def context = parserContext(content)
        context.parserId = DocumentParser.ID

        when:
        def doc = Parser.parse(context)

        then:
        doc == builder.document(title: 'Document Title') {
            header {
            }

            preamble {
                para {
                    text '''line1
line2'''
                }
                para {
                    text '''line3
line4
line5'''
                }
            }
        }
    }
}
