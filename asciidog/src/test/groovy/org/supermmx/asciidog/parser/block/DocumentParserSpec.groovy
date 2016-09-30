package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Node

class DocumentParserSpec extends AsciidogSpec {
    def parser = new DocumentParser()

    def 'checkStart: good'() {
        given:

        def line = '= Title'
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def isStart = parser.checkStart(line, header, true)

        then:
        isStart
        header.properties[(DocumentParser.HEADER_PROPERTY_DOCUMENT_TITLE)] == 'Title'
    }

    def 'document: with sections'() {
        given:
        def content = '''= Document Title

== Section Title

== Another Section
'''
        def eDoc = builder.document(title: 'Document Title') {
            header {
            }

            section(level: 1, title: 'Section Title')
            section(level: 1, title: 'Another Section')
        }
        def context = parserContext(content)

        when:
        def doc = Parser.parse(context)

        then:
        doc == eDoc
    }
}
