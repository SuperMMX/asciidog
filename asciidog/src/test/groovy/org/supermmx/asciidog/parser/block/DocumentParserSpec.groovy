package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
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
}
