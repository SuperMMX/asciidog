package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Node
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

    def 'static: block attributes'() {
        expect:
        attributes == Parser.isBlockAttributes(line)
        (attributes?.keySet()) as String[] == keys

        where:
        line                  | attributes       | keys
        null                  | null             | null
        ''                    | null             | null
        '[]'                  | null             | null
        '[style]'             | [style:null]     | [ 'style' ]
        '[,style,]'           | ['': null, 'style': null]     | [ '', 'style']
        '[ abc =, = abc]'     | ['abc': '', '': 'abc']     | [ 'abc', '' ]
        '[ quote,  this is a quote  ]' | [quote: null, 'this is a quote': null] | [ 'quote', 'this is a quote' ]
        '[ \'at,"tr  \'=\'a, "value"  \',  style  , "  new,\'attr" = "a, \'new\' value  " ]' | ['at,"tr  ': 'a, "value"  ', style: null, '  new,\'attr': "a, 'new' value  "] | [ 'at,"tr  ', 'style', '  new,\'attr']
    }

    def 'static: is list'() {
        expect:
        result == Parser.isList(line)

        where:
        line               | result
        null               | [ null, null, null, -1, null ]
        ''                 | [ null, null, null, -1, null ]
        '  == abc '        | [ null, null, null, -1, null ]
        '*  line  '        | [ Node.Type.UNORDERED_LIST, '', '*', 1, 'line  ' ]
        '   ...  line  '   | [ Node.Type.ORDERED_LIST, '   ', '.', 3, 'line  ' ]
        '  -  line  '      | [ Node.Type.UNORDERED_LIST, '  ', '-', 1, 'line  ' ]
    }

    def 'static: is comment line'() {
        expect:
        comment == Parser.isCommentLine(line)

        where:
        comment         | line
        null            | null
        null            | ''
        null            | '////'
        ' comment'      | '// comment'
        'comment'       | '//comment'
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
