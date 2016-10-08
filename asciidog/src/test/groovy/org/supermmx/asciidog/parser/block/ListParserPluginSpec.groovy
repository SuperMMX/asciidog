package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.block.ListParserPlugin.*

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

class ListParserPluginSpec extends AsciidogSpec {
    def parser

    def setup() {
        parser = Spy(ListParserPlugin) {
            createList() >> new OrderedList()
        }

        parser.nodeType = Node.Type.ORDERED_LIST
    }

    def 'checkStart: false'() {
        expect:
        isStart == parser.checkStart(line, header, false)

        where:
        isStart | line       | header
        false   | 'abc'      | new BlockHeader(type: Node.Type.PARAGRAPH)
        false   | '* abc'    | new BlockHeader(type: Node.Type.UNORDERED_LIST)
        false   | 'abc'      | new BlockHeader()
        false   | '* abc'    | new BlockHeader()
    }

    def 'checkStart: true'() {
        given:
        def line = '  ..  abc   '
        def header = new BlockHeader()

        when:
        def isStart = parser.checkStart(line, header, false)

        then:
        isStart
        header == new BlockHeader(type: Node.Type.ORDERED_LIST,
                                  properties: [
                                      (LIST_LEAD): '  ',
                                      (LIST_MARKER): '.',
                                      (LIST_MARKER_LEVEL): 2,
                                      (LIST_CONTENT_START): 6,
                                  ])
    }

}
