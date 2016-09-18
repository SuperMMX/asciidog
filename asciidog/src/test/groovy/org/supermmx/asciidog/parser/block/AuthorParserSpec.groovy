package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Authors
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node

class AuthorParserSpec extends AsciidogSpec {
    def authorParser = new AuthorParser()

    def 'regex: single author name'() {
        expect:
        AuthorParser.AUTHOR_NAME_PATTERN.matcher(name).matches() == result

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
        AuthorParser.AUTHOR_PATTERN.matcher(author)[0] == result

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
        AuthorParser.AUTHOR_LINE_PATTERN.matcher(line).matches() == result

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
        AuthorParser.createAuthor(authorText) == author

        where:
        authorText | author
        'Name' | new Author(first: 'Name', middle: null, last: null, initials: 'N', email: null)
        ' First   Last   <abc@def.com> ' | new Author(first: 'First', middle: null, last: 'Last', initials: 'FL', email: 'abc@def.com')
        'First.   Middle-    Last_  <test@email.com>  ' | new Author(first: 'First.', middle: 'Middle-', last: 'Last_', initials: 'FML', email: 'test@email.com')
    }

    def 'checkStart: not expected'() {
        given:
        def line = 'Stuart Rackham <founder@asciidoc.org>'
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def isStart = authorParser.checkStart(line, header, false)

        then:
        !isStart
    }

    def 'checkStart: single author'() {
        given:
        def line = 'Stuart Rackham <founder@asciidoc.org>'
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def isStart = authorParser.checkStart(line, header, true)

        then:
        isStart
        header.properties[(AuthorParser.HEADER_PROPERTY_AUTHOR_LINE)] == line
    }

    def 'parse authors'() {
        given:
        def context = parserContext(content)

        when:
        def authors = authorParser.parse(context)

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
            new Authors(children: [ new Author(first: '张三', middle: null, last: null, initials: '张', email: 'zhang.san@email.com') ]),
            new Authors(children: [ new Author(first: 'Name', middle: null, last: null, initials: 'N', email: null) ]),
            new Authors(children: [ new Author(first: 'First', middle: null, last: 'Last', initials: 'FL', email: 'abc@def.com') ]),
            new Authors(children: [
                            new Author(first: 'First', middle: null, last: 'Last', initials: 'FL', email: 'abc@def.com'),
                            new Author(first: 'First.', middle: 'Middle-', last: 'Last_', initials: 'FML', email: 'test@email.com')
                        ])
        ]
    }
}
