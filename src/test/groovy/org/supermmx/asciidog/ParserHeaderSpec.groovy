package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Header

import spock.lang.*

class ParserSpec extends Specification {
    static def HEADER_NULL = null
    static def HEADER_EMPTY = ''
    static def HEADER_EMPTY_LINE = '''
'''
    static def HEADER_NO_CONTENT = '''=
'''
    static def HEADER_SECTION_2 = '''=== abc def
'''

    def 'null header'() {
        given:
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader
        reader.nextLine()

        when:
        def header = parser.parseHeader()

        then:
        header == resHeader

        where:
        content        | resHeader
        HEADER_NULL    | null
        HEADER_EMPTY   | null
        HEADER_EMPTY_LINE | null
        HEADER_NO_CONTENT | null
        HEADER_SECTION_2  | null
    }

    static def HEADER_TITLE_SIMPLE = '''= Title
'''
    static def HEADER_TITLE_SPACE = '''= Title With	Space   
'''

    def 'header title'() {
        given:
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader
        reader.nextLine()

        when:
        def header = parser.parseHeader()

        then:
        header != null
        header.title == title

        where:
        content             | title
        HEADER_TITLE_SIMPLE | 'Title'
        HEADER_TITLE_SPACE  | 'Title With	Space'
    }
}
