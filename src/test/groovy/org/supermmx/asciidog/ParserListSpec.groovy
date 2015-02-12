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
    def 'unordered list with one line paragraph'() {
        given:
        def content = '* list item'

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def list = parser.parseList(new Block())

        then:
        list instanceof UnOrderedList
        list.type == Node.Type.UNORDERED_LIST
        list.blocks.size() == 1
        list.blocks[0] instanceof ListItem
        list.blocks[0].blocks.size() == 1
        list.blocks[0].blocks[0] instanceof Paragraph
        list.blocks[0].blocks[0].lines == [ 'list item' ]
    }

    def 'ordered list with multiple-line paragraph'() {
        given:
        def content = '''
. list item with
multiple lines
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def list = parser.parseList(new Block())

        then:
        list instanceof OrderedList
        list.type == Node.Type.ORDERED_LIST
        list.blocks.size() == 1
        list.blocks[0] instanceof ListItem
        list.blocks[0].blocks.size() == 1
        list.blocks[0].blocks[0] instanceof Paragraph
        list.blocks[0].blocks[0].lines == [ 'list item with', 'multiple lines' ]
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
'''

        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def list = parser.parseList(new Block())

        then:
        list instanceof UnOrderedList
        list.type == Node.Type.UNORDERED_LIST
        list.blocks.size() == 1
        list.blocks[0] instanceof ListItem
        list.blocks[0].blocks.size() == 2
        list.blocks[0].blocks[0] instanceof Paragraph
        list.blocks[0].blocks[0].lines == [ 'list item with', 'multiple lines' ]
        list.blocks[0].blocks[1] instanceof Paragraph
        list.blocks[0].blocks[1].lines == [ 'line1', 'line2', 'line3' ]
    }
}
