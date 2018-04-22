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

    def 'createBlock: level 1'() {
        given:
        def context = parserContext('')
        def header = new BlockHeader(type: Node.Type.ORDERED_LIST,
                                     properties: [
                                         (LIST_LEAD): '  ',
                                         (LIST_MARKER): '.',
                                         (LIST_MARKER_LEVEL): 2,
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
        context.childParserProps.expected == true
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
        context.childParserProps.expected == true
        childParser == ListItemParser.ID
        context.paragraphEndingCheckers.size() == 1
    }

    def 'toEndParagraph: list continuation' () {
        expect:
        parser.toEndParagraph(parserContext('+'))
        parser.toEndParagraph(parserContext('  +'))
    }

    def 'toEndParagraph: unordered list' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.UNORDERED_LIST)

        expect:
        parser.toEndParagraph(context)
    }

    def 'toEndParagraph: ordered list' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.ORDERED_LIST)

        expect:
        parser.toEndParagraph(context)
    }

    def 'toEndParagraph: paragraph' () {
        given:
        def context = parserContext('')

        context.blockHeader = new BlockHeader(type: Node.Type.PARAGRAPH)

        expect:
        !parser.toEndParagraph(context)
    }

    def 'document: star elements with continuation'() {
        given:
        def content = '''
* Foo
123
+
456

* Boo
+
789

outside list
'''
        def eDoc = builder.document {
            ul(lead: '', level: 1, marker: '*', markerLevel: 1) {
                item {
                    para {
                        text 'Foo\n123'
                    }
                    para {
                        text '456'
                    }
                }
                item {
                    para {
                        text 'Boo'
                    }
                    para {
                        text '789'
                    }
                }
            }
            para {
                text 'outside list'
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: dot elements without blank lines'() {
        given:
        def content = '''
. Foo
. Boo
. Blech
'''
        def eDoc = builder.document {
            ol(lead: '', level: 1, marker: '.', markerLevel: 1) {
                item {
                    para {
                        text 'Foo'
                    }
                }
                item {
                    para {
                        text 'Boo'
                    }
                }
                item {
                    para {
                        text 'Blech'
                    }
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: dot elements with multiple lines'() {
        given:
        def content = '''
. Foo
123

. Boo
456

abc def
'''
        def eDoc = builder.document {
            ol(lead: '', level: 1, marker: '.', markerLevel: 1) {
                item {
                    para {
                        text 'Foo\n123'
                    }
                }
                item {
                    para {
                        text 'Boo\n456'
                    }
                }
            }

            para {
                text 'abc def'
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: dash elements with no blank lines'() {
        given:
        def content = '''
- Foo
- Boo
- Blech
'''
        def eDoc = builder.document {
            ul(lead: '', level: 1, marker: '-', markerLevel: 1) {
                item {
                    para {
                        text 'Foo'
                    }
                }
                item {
                    para {
                        text 'Boo'
                    }
                }
                item {
                    para {
                        text 'Blech'
                    }
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: indented dash elements using spaces'() {
        given:
        def content = '''
 - Foo
 - Boo
 - Blech
'''
        def eDoc = builder.document {
            ul(lead: ' ', level: 1, marker: '-', markerLevel: 1) {
                item {
                    para {
                        text 'Foo'
                    }
                }
                item {
                    para {
                        text 'Boo'
                    }
                }
                item {
                    para {
                        text 'Blech'
                    }
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: indented dash elements using tabs'() {
        given:
        def content = '''
\t-\tFoo
\t-\tBoo
\t-\tBlech
'''
        def eDoc = builder.document {
            ul(lead: '\t', level: 1, marker: '-', markerLevel: 1) {
                item {
                    para {
                        text 'Foo'
                    }
                }
                item {
                    para {
                        text 'Boo'
                    }
                }
                item {
                    para {
                        text 'Blech'
                    }
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'document: section after list'() {
        given:
        def content = '''
== Section Title

* item1
* item2

=== Subsection Title
'''
        def eDoc = builder.document {
            section(title: 'Section Title', level: 1) {
                ul(lead: '', level: 1, marker: '*', markerLevel: 1) {
                    item {
                        para {
                            text 'item1'
                        }
                    }
                    item {
                        para {
                            text 'item2'
                        }
                    }
                }
                section(title: 'Subsection Title', level: 2) {
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }
}
