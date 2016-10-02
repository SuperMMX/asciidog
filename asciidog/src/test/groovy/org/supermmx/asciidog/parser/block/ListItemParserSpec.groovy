package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

class ListItemParserSpec extends AsciidogSpec {
    def parser = new ListItemParser()

    def 'checkStart: check start'(){
        expect:
        isStart == parser.checkStart(line, new BlockHeader(type: type), expected)

        where:
        isStart | line | type                     | expected
        true    | ''   | Node.Type.ORDERED_LIST   | true
        true    | ''   | Node.Type.UNORDERED_LIST | true
        false   | ''   | Node.Type.PARAGRAPH      | true
        false   | ''   | Node.Type.ORDERED_LIST   | false
        false   | ''   | Node.Type.UNORDERED_LIST | false
    }

    def 'standalone: simple paragraph'() {
        given:
        def content = '''. abc
'''
        def expListItem = builder.item {
            para(lines: ['abc'])
        }
        def context = parserContext(content)
        context.with {
            blockHeader = new BlockHeader()
            blockHeader.with {
                type = Node.Type.ORDERED_LIST
                properties[LIST_MARKER] = '.'
                properties[LIST_MARKER_LEVEL] = 1
                properties[LIST_CONTENT_START] = 2
            }
            expected = true
        }
        context.parserId = parser.id
        context.blockHeader.parserId = OrderedListParser.ID

        when:
        def listItem = Parser.parse(context)

        then:
        listItem == expListItem
    }
}
