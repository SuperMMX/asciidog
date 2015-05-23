package org.supermmx.asciidog

import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

class ParserHeaderSpec extends AsciidogSpec {
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

    def 'regex: single author name'() {
        expect:
        Parser.AUTHOR_NAME_PATTERN.matcher(name).matches() == result

        where:
        name      | result
        'Name'    | true
        'Name.'   | true
        'Na-me'   | true
        'Name_'   | true
        'Na\'me'  | true
    }

    def 'regex: single author'() {
        expect:
        Parser.AUTHOR_PATTERN.matcher(author)[0] == result

        where:
        author            |  result
        '张三'           |  [ '张三', '张三', null, null, null ]
        'First'           |  [ 'First', 'First', null, null, null ]
        ' First. '           |  [ ' First. ', 'First.', null, null, null ]
        ' First  Last '   |  [ ' First  Last ', 'First', 'Last', null, null ]
        ' First.  Middle-Name.  Last ' | [ ' First.  Middle-Name.  Last ', 'First.', 'Middle-Name.', 'Last', null]
        ' First-   Middle.Name.   Last   <abc@def.com>  ' | [ ' First-   Middle.Name.   Last   <abc@def.com>  ', 'First-', 'Middle.Name.', 'Last', 'abc@def.com' ]
    }

    def 'regex: author line'() {
        expect:
        Parser.AUTHOR_LINE_PATTERN.matcher(line).matches() == result

        where:
        line            |  result
        '张三'          |  true
        '张三 <zhang.san@email.com>'          |  true
        'First Last <abc@def.com>' |  true
        'First <abc@def.com> ; Second Last <test@test.org>' |  true
        'First Second Third Forth' | false
    }

    def 'static: create author'() {
        expect:
        Parser.createAuthor(authorText) == author

        where:
        authorText | author
        'Name' | new Author('Name', 'Name', null, null, 'N', null)
        ' First   Last   <abc@def.com> ' | new Author('First Last', 'First', null, 'Last', 'FL', 'abc@def.com')
        'First.   Middle-    Last_  <test@email.com>  ' | new Author('First. Middle- Last_', 'First.', 'Middle-', 'Last_', 'FML', 'test@email.com')
    }

    def 'parse authors'() {
        given:
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def authors = parser.parseAuthors()

        then:
        authors == expectedAuthors

        where:
        content << [
            '张三 <zhang.san@email.com>',
            'Name',
            ' First   Last   <abc@def.com> ',
            'First Last <abc@def.com> ; First.   Middle-    Last_  <test@email.com>  '
        ]
        expectedAuthors << [
            [ new Author('张三', '张三', null, null, '张', 'zhang.san@email.com') ],
            [ new Author('Name', 'Name', null, null, 'N', null) ],
            [ new Author('First Last', 'First', null, 'Last', 'FL', 'abc@def.com') ],
            [
                new Author('First Last', 'First', null, 'Last', 'FL', 'abc@def.com'),
                new Author('First. Middle- Last_', 'First.', 'Middle-', 'Last_', 'FML', 'test@email.com')
            ]
        ]
    }

}
