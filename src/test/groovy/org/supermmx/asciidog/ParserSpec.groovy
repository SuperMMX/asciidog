package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ParserSpec extends Specification {
    def 'static: is attribute, value of single line'() {
        expect:
        [ name, value ] == Parser.isAttribute(line)

        where:
        name    | value       | line
        'attr'  | 'a value'   | ':attr: a value'
        'attr'  | 'a value'   | ':attr:   a value'
        'attr'  | null        | ':attr:'
        'at tr'  | ''          | ':at tr:  '
        '!at tr' | null        | ':!at tr:'
        null    | null        | null
        null    | null        | ''
        null    | null        | 'abcdef'
        null    | null        | '* abc'
        null    | null        | '== abc'
    }

    def 'static: is block anchor'() {
        expect:
        [ id, ref ] == Parser.isBlockAnchor(line)

        where:
        id       | ref        | line
        'id'     | null       | '[[id]]'
        'id'     | 'ref'      | '[[id, ref]]'
        ':id-'   | '#- '      | '[[:id-, #- ]]'
        '_i.d'   | '_-:. '    | '[[_i.d, _-:. ]]'
    }

    def 'static: is block title'() {
        expect:
        title == Parser.isBlockTitle(line)

        where:
        title           | line
        null            | null
        null            | ''
        null            | '. abc def'
        null            | '.'
        null            | '== section'
        'block title'   | '.block title'
    }

    def 'parse: Paragraph'() {
        given:
        def content = '''

line1
line2
line3

line4
line5


'''
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def para = parser.parseParagraph(new Block())

        then:
        para.lines == [ 'line1', 'line2', 'line3']

        when:
        para = parser.parseParagraph(new Block())

        then:
        para.lines == [ 'line4', 'line5']
    }
}
