package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.UnOrderedList

import spock.lang.*

class ParserListSpec extends Specification {
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
    }

    def 'unordered list with one line paragraph'() {
        given:
        def content = '* list item'

        def expectedList = builder.unOrderedList(marker: '*',
                                                 markerLevel: 1,
                                                 level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['list item'])
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }

    def 'ordered list with multiple-line paragraph'() {
        given:
        def content = '''
. list item with
multiple lines

new paragraph
'''

        def expectedList = builder.orderedList(marker: '.',
                                               markerLevel: 1,
                                               level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['list item with', 'multiple lines'])
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }

    def 'unordered list with list continuation'() {
        given:
        def content = '''
* list item with
multiple lines
+
line1
line2
line3

== section
'''

        def expectedList = builder.unOrderedList(marker: '*',
                                                 markerLevel: 1,
                                                 level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['list item with',
                                          'multiple lines']),
                        paragraph(lines: ['line1', 'line2', 'line3'])
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }

    def 'ordered list with multiple items'() {
        given:
        def content = '''
. item1
. item2
. item3

== section
'''

        def expectedList = builder.orderedList(marker: '.',
                                               markerLevel: 1,
                                               level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['item1'])
                    ]
                },
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['item2'])
                    ]
                },
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['item3'])
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }

    def 'nested list with different markers'() {
        given:
        def content = '''
. item1
* item2
- item3

new paragraph
'''

        def expectedList = builder.orderedList(marker: '.',
                                               markerLevel: 1,
                                               level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['item1']),
                        unOrderedList(marker: '*',
                                      markerLevel: 1,
                                      level: 0) {
                            current.blocks = [
                                listItem() {
                                    current.blocks = [
                                        paragraph(lines: ['item2']),
                                        unOrderedList(marker: '-',
                                                      markerLevel: 1,
                                                      level: 0) {
                                            current.blocks = [
                                                listItem() {
                                                    current.blocks = [
                                                        paragraph(lines: ['item3'])
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }

    def 'nested list with same marker'() {
        given:
        def content = '''
. item1
.. item2
* item3

new paragraph
'''

        def expectedList = builder.orderedList(marker: '.',
                                               markerLevel: 1,
                                               level: 0) {
            current.blocks = [
                listItem() {
                    current.blocks = [
                        paragraph(lines: ['item1']),
                        orderedList(marker: '.',
                                    markerLevel: 2,
                                    level: 0) {
                            current.blocks = [
                                listItem() {
                                    current.blocks = [
                                        paragraph(lines: ['item2']),
                                        unOrderedList(marker: '*',
                                                      markerLevel: 1,
                                                      level: 0) {
                                            current.blocks = [
                                                listItem() {
                                                    current.blocks = [
                                                        paragraph(lines: ['item3'])
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:

        def list = parser.parseList(new Block())

        then:

        list == expectedList
    }
}
