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

class ParserListSpec extends AsciidogSpec {
    def 'unordered list with one line paragraph'() {
        given:
        def content = '* list item'

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1, level: 1) {
            item {
                para {
                    text 'list item'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'ordered list with multiple-line paragraph'() {
        given:
        def text1 = '''list item with
multiple lines'''
        def text2 = 'new paragraph'
        def content = """
. $text1

$text2
"""

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text text1
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'unordered list with list continuation'() {
        given:
        def text1 = '''list item with
multiple lines'''
        def text2 = '''line1
line2
line3'''
        def content = """
* $text1
+
$text2

== section
"""

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text text1
                }
                para {
                    text text2
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'ordered list with multiple items'() {
        given:
        def content = '''
. item1
. item2
. item3

== section
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
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
            item {
                para {
                    text 'item3'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'nested list with different markers'() {
        given:
        def content = '''
. item1
* item2
- item3

new paragraph
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1'
                }
                ul(lead: '', marker: '*',
                   markerLevel: 1, level: 2) {
                    item {
                        para {
                            text 'item2'
                        }
                        ul(lead: '', marker: '-',
                           markerLevel: 1, level: 3) {
                            item {
                                para {
                                    text 'item3'
                                }
                            }
                        }
                    }
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'nested list with same marker'() {
        given:
        def content = '''
. item1
.. item2
* item3

new paragraph
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1'
                }

                ol(lead: '', marker: '.',
                   markerLevel: 2,
                   level: 2) {
                    item {
                        para {
                            text 'item2'
                        }

                        ul(lead: '', marker: '*',
                           markerLevel: 1,
                           level: 3) {
                            item {
                                para {
                                    text 'item3'
                                }
                            }
                        }
                    }
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'nested list with same marker with blank lines'() {
        given:
        def content = '''
. item1
paragraph1

.. item2
paragraph2

... item3
paragraph3

new paragraph
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
                item {
                    para {
                        text 'item1\nparagraph1'
                    }

                    ol(lead: '', marker: '.',
                       markerLevel: 2,
                       level: 2) {
                        item {
                            para {
                                text 'item2\nparagraph2'
                            }

                            ol(lead: '', marker: '.',
                               markerLevel: 3,
                               level: 3) {
                                item {
                                    para {
                                        text 'item3\nparagraph3'
                                    }
                                }
                            }
                        }
                    }
                }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'unordered list with multiple items with multiple lines'() {
        given:
        def content = '''
* item1
paragraph1


* item2
paragraph2
+
new paragraph
with multiple lines


* item3
paragraph3

[[id]
== section
'''

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1\nparagraph1'
                }
            }

            item {
                para {
                    text 'item2\nparagraph2'
                }

                para {
                    text 'new paragraph\nwith multiple lines'
                }
            }

            item {
                para {
                    text 'item3\nparagraph3'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'ordered list ended with comment line'() {
        given:
        def content = '''
. item1
. item2

// this is a comment

. item3
. item4
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
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

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'unordered list ended with comment line'() {
        given:
        def content = '''
* item1
* item2

// this is a comment

. item3
. item4
'''

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1,
                                      level: 1) {
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

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'same level items with nested list with different marker'() {
        given:
        def content = '''
. item1
* item2
. item3

new paragraph
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1'
                }

                ul(lead: '', marker: '*',
                   markerLevel: 1,
                   level: 2) {
                    item {
                        para {
                            text 'item2'
                        }
                    }
                }
            }

            item {
                para {
                    text 'item3'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'indented dash elements using spaces'() {
        given:
        def content = '''
 - Foo
 - Bar
 - Baz
'''

        def expectedList = builder.ul(lead: ' ', marker: '-',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'Foo'
                }
            }

            item {
                para {
                    text 'Bar'
                }
            }

            item {
                para {
                    text 'Baz'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'indented nested list continuation'() {
        given:
        def content = '''
. item1
 * item2
 +
item3
+
item4
'''

        def expectedList = builder.ol(lead: '', marker: '.',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1'
                }

                ul(lead: ' ', marker: '*',
                   markerLevel: 1, level: 2) {
                    item {
                        para {
                            text 'item2'
                        }

                        para {
                            text 'item3'
                        }
                    }
                }

                para {
                    text 'item4'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'top level list headers'() {
        given:
        def content = '''
:name: value
.title
[[id]]
[attribute]
* item1
* item2
'''

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1,
                                      level: 1,
                                      title: 'title',
                                      id: 'id',
                                      attributes: ['attribute':null]) {
            attribute('name', 'value')
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

        expect:
        parser(content).parseList(new Block()) == expectedList
    }

    def 'headers for the list items'() {
        given:
        def content = '''
* item1
:name: value
.title
[[id]]
[attribute]
* item2
'''

        def expectedList = builder.ul(lead: '', marker: '*',
                                      markerLevel: 1,
                                      level: 1) {
            item {
                para {
                    text 'item1'
                }
            }
            item(id: 'id', title: 'title',
                 attributes: ['attribute':null]) {
                attribute('name', 'value')
                para {
                    text 'item2'
                }
            }
        }

        expect:
        parser(content).parseList(new Block()) == expectedList
    }
}
