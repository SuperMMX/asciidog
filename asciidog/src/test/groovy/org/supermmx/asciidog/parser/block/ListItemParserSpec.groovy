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

class ListItemParserSpec extends AsciidogSpec {
    def parser = new ListItemParser()

    def 'checkStart: check start'() {
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

    def 'isListItem: in section'() {
        given:
        def section = new Section()

        expect:
        !parser.isListItem(section, '', '*', 1)
        !parser.isListItem(section, '', '*', 2)
        !parser.isListItem(section, '', '.', 1)
        !parser.isListItem(section, '', '.', 2)
    }

    def 'isListItem: multiple level'() {
        given:
        def item3 = new ListItem()
        def list3 = new OrderedList(lead: '', level: 3,
                                    marker: '.', markerLevel: 2)
        def item2 = new ListItem()
        def list2 = new OrderedList(lead: '', level: 2,
                                    marker: '.', markerLevel: 1)

        def item1 = new ListItem()
        def list1 = new UnOrderedList(lead: '', level: 1,
                                      marker: '*', markerLevel: 1)

        item3.parent = list3
        list3.parent = item2
        item2.parent = list2
        list2.parent = item1
        item1.parent = list1

        expect:
        parser.isListItem(item3, '', '.', 2)
        parser.isListItem(item3, '', '.', 1)
        parser.isListItem(item3, '', '*', 1)
        !parser.isListItem(item3, '', '.', 3)
        !parser.isListItem(item3, '', '*', 2)
    }

    def 'nextChildParser: first child'() {
        given:
        def item = new ListItem()
        def context = parserContext('')
        context.block = item

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
    }

    def 'nextChildParser: section'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def header = new BlockParserPlugin.BlockHeader(parserId: SectionParser.ID,
                                                       type: Node.Type.SECTION)

        context.block = item
        context.blockHeader = header

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'nextChildParser: child list'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def list = new UnOrderedList(lead: '', level: 1,
                                     marker: '*', markerLevel: 1)
        list << item
        item.parent = list

        def header = new BlockParserPlugin.BlockHeader()
        header.with {
           parserId = OrderedListParser.ID
           type = Node.Type.ORDERED_LIST
           properties[LIST_LEAD] = ''
           properties[LIST_MARKER] = '.'
           properties[LIST_MARKER_LEVEL] = 1
        }

        context.block = item
        context.blockHeader = header

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
        parser.getNextChildParser(context) == OrderedListParser.ID
    }

    def 'nextChildParser: non-child list'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def list = new UnOrderedList(lead: '', level: 1,
                                     marker: '*', markerLevel: 1)
        list << item
        item.parent = list

        def header = new BlockParserPlugin.BlockHeader()
        header.with {
           parserId = OrderedListParser.ID
           type = Node.Type.ORDERED_LIST
           properties[LIST_LEAD] = ''
           properties[LIST_MARKER] = '*'
           properties[LIST_MARKER_LEVEL] = 1
        }

        context.block = item
        context.blockHeader = header

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'nextChildParser: non-list'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def list = new UnOrderedList(lead: '', level: 1,
                                     marker: '*', markerLevel: 1)
        list << item
        item.parent = list

        def header = new BlockParserPlugin.BlockHeader(parserId: ParagraphParser.ID,
                                                       type: Node.Type.PARAGRAPH)

        context.block = item
        context.blockHeader = header

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
    }

    def 'nextChildParser: blank header'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def list = new UnOrderedList(lead: '', level: 1,
                                     marker: '*', markerLevel: 1)
        list << item
        item.parent = list

        context.block = item
        context.blockHeader = new BlockParserPlugin.BlockHeader()

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'nextChildParser: null header'() {
        given:
        def context = parserContext('')
        def item = new ListItem()
        def list = new UnOrderedList(lead: '', level: 1,
                                     marker: '*', markerLevel: 1)
        list << item
        item.parent = list

        context.block = item
        context.blockHeader = null

        expect:
        parser.getNextChildParser(context) == ParagraphParser.ID
        parser.getNextChildParser(context) == null
    }

    def 'standalone: simple paragraph'() {
        given:
        def content = '''. abc
'''
        def expListItem = builder.item {
            para {
                text 'abc'
            }
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
