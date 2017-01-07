package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec

class BlockParserPluginSpec extends AsciidogSpec {
    def 'static: is block anchor'() {
        expect:
        [ id, ref ] == BlockParserPlugin.isBlockAnchor(line)

        where:
        id       | ref        | line
        'id'     | null       | '[[id]]'
        'id'     | 'ref'      | '[[id, ref]]'
        ':id-'   | '#- '      | '[[:id-, #- ]]'
        '_i.d'   | '_-:. '    | '[[_i.d, _-:. ]]'
    }

    def 'static: is block title'() {
        expect:
        title == BlockParserPlugin.isBlockTitle(line)

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
        attributes == BlockParserPlugin.isBlockAttributes(line)
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

    def 'static: is comment line'() {
        expect:
        comment == BlockParserPlugin.isCommentLine(line)

        where:
        comment         | line
        null            | null
        null            | ''
        null            | '////'
        ' comment'      | '// comment'
        'comment'       | '//comment'
    }
}
