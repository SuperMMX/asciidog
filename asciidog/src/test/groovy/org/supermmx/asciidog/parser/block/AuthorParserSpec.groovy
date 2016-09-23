package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
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

    def 'checkStart: expected invalid'() {
        given:
        def line = 'Stuart Rackham'
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def isStart = authorParser.checkStart(line, header, false)

        then:
        !isStart
    }

    def 'checkStart: expected single author line'() {
        given:
        def line = 'Stuart Rackham <founder@asciidoc.org>'
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def isStart = authorParser.checkStart(line, header, true)

        then:
        isStart
        header.properties[(AuthorParser.HEADER_PROPERTY_AUTHOR_LINE)] == line
    }

    def 'standalone: parse authors'() {
        given:
        def context = parserContext(content)
        context.parser = authorParser

        when:
        def authors = Parser.parse(context)

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
            builder.authors { author '张三 <zhang.san@email.com>' },
            builder.authors { author 'Name' },
            builder.authors { author 'First Last <abc@def.com>' },
            builder.authors {
                author 'First Last <abc@def.com>'
                author 'First. Middle- Last_ <test@email.com>'
            }
        ]
    }

    def 'document: parse authors'() {
        given:
        def content = '''= Document Title
Stuart Rackham <founder@asciidoc.org>; Dan Allen <dan.j.allen@gmail.com>'''
        def expectedDoc = builder.document(title: 'Document Title') {
            header {
                authors {
                    author 'Stuart Rackham <founder@asciidoc.org>'
                    author 'Dan Allen <dan.j.allen@gmail.com>'
                }
            }
        }

        def context = parserContext(content)
        context.parser = new DocumentParser()

        when:
        def doc = Parser.parse(context)

        then:
        doc == expectedDoc
    }
}
