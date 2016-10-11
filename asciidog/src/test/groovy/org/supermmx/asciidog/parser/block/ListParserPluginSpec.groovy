package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.block.ListParserPlugin.*

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
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

    def 'createBlock: level 1'() {
        given:
        def context = parserContext('')
        def header = new BlockHeader(type: Node.Type.ORDERED_LIST,
                                     properties: [
                                         (LIST_LEAD): '  ',
                                         (LIST_MARKER): '.',
                                         (LIST_MARKER_LEVEL): 2,
                                         (LIST_CONTENT_START): 6,
                                     ],
                                     id: 'list_id', title: 'List Title',
                                     attributes:[ 'attr1': 'value1', 'attr2': 2])

        def parent = new Block()

        when:
        def list = parser.createBlock(context, parent, header)

        then:
        list == builder.ol(lead: '  ', level: 1, marker: '.', markerLevel: 2,
                           id: 'list_id', title: 'List Title',
                           attributes: [ 'attr1': 'value1', 'attr2': 2])
        context.blockHeader == new BlockHeader(type: Node.Type.ORDERED_LIST,
                                               properties: [
                                                   (LIST_LEAD): '  ',
                                                   (LIST_MARKER): '.',
                                                   (LIST_MARKER_LEVEL): 2,
                                                   (LIST_CONTENT_START): 6,
                                               ])
        context.keepHeader == true
        context.paragraphEndingCheckers.size() == 1
        context.paragraphEndingCheckers[0].id == parser.id

    }

    def 'createBlock: level 3'() {
        given:
        def context = parserContext('')
        def header = new BlockHeader(type: Node.Type.ORDERED_LIST,
                                     properties: [
                                         (LIST_LEAD): '  ',
                                         (LIST_MARKER): '.',
                                         (LIST_MARKER_LEVEL): 2,
                                         (LIST_CONTENT_START): 6,
                                     ],
                                     id: 'list_id', title: 'List Title',
                                     attributes:[ 'attr1': 'value1', 'attr2': 2])

        def item2 = new ListItem()
        def list2 = new UnOrderedList(lead: '', level: 2,
                                      marker: '*', markerLevel: 1)
        item2.parent = list2

        when:
        def list = parser.createBlock(context, item2, header)

        then:
        list == builder.ol(lead: '  ', level: 3, marker: '.', markerLevel: 2,
                           id: 'list_id', title: 'List Title',
                           attributes: [ 'attr1': 'value1', 'attr2': 2])
        context.blockHeader == new BlockHeader(type: Node.Type.ORDERED_LIST,
                                               properties: [
                                                   (LIST_LEAD): '  ',
                                                   (LIST_MARKER): '.',
                                                   (LIST_MARKER_LEVEL): 2,
                                                   (LIST_CONTENT_START): 6,
                                               ])
        context.keepHeader == true
        context.paragraphEndingCheckers.size() == 1
        context.paragraphEndingCheckers[0].id == parser.id

    }

    def 'nextChildParser: null header'() {
        given:
        def context = parserContext('')
        context.blockHeader = null
        context.paragraphEndingCheckers << parser

        expect:
        parser.getNextChildParser(context) == null
        context.paragraphEndingCheckers.size() == 0
    }

    def 'nextChildParser: blank header'() {
        given:
        def context = parserContext('')
        context.blockHeader = new BlockHeader()
        context.paragraphEndingCheckers << parser

        expect:
        parser.getNextChildParser(context) == null
        context.paragraphEndingCheckers.size() == 0
    }

    def 'nextChildParser: non-list'() {
        given:
        def context = parserContext('')
        context.blockHeader = new BlockHeader(type: Node.Type.PARAGRAPH)
        context.paragraphEndingCheckers << parser

        expect:
        parser.getNextChildParser(context) == null
        context.paragraphEndingCheckers.size() == 0
    }

    def 'nextChildParser: ordered list'() {
        given:
        def context = parserContext('')
        context.blockHeader = new BlockHeader(type: Node.Type.ORDERED_LIST)
        context.paragraphEndingCheckers << parser

        when:
        def childParser = parser.getNextChildParser(context)

        then:
        context.expected == true
        childParser == ListItemParser.ID
        context.paragraphEndingCheckers.size() == 1
    }

    def 'nextChildParser: unordered list'() {
        given:
        def context = parserContext('')
        context.blockHeader = new BlockHeader(type: Node.Type.UNORDERED_LIST)
        context.paragraphEndingCheckers << parser

        when:
        def childParser = parser.getNextChildParser(context)

        then:
        context.expected == true
        childParser == ListItemParser.ID
        context.paragraphEndingCheckers.size() == 1
    }

    def 'toEndParagraph: list continuation' () {
        expect:
        parser.toEndParagraph(null, '+')
        parser.toEndParagraph(null, '  +')
    }

    def 'toEndParagraph: unordered list' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.UNORDERED_LIST)

        expect:
        parser.toEndParagraph(context, '')
    }

    def 'toEndParagraph: ordered list' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.ORDERED_LIST)

        expect:
        parser.toEndParagraph(context, '')
    }

    def 'toEndParagraph: paragraph' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.PARAGRAPH)

        expect:
        !parser.toEndParagraph(context, '')
    }
}
