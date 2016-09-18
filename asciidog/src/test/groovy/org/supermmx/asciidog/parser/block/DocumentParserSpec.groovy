package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.ast.Node

class DocumentParserSpec extends AsciidogSpec {
    def documentParser = new DocumentParser()

    def 'good start'() {
        given:

        def line = '= Title'
        def header = new BlockParserPlugin.BlockHeader(type: Node.Type.SECTION,
                                                       properties: [(SectionParser.HEADER_PROPERTY_SECTION_LEVEL): 0,
                                                                    (SectionParser.HEADER_PROPERTY_SECTION_TITLE): 'Title'])
        when:
        def isStart = documentParser.checkStart(line, header, true)

        then:
        isStart
        header.properties[(DocumentParser.HEADER_PROPERTY_DOCUMENT_TITLE)] == 'Title'
    }
}
