package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec

class CommentLineParserSpec extends AsciidogSpec {
    def 'document: simple comment line'() {
        given:

        def content = '''
First paragraph

// this is a comment line

Second paragraph
'''
        def edoc = builder.document {
            para {
                text 'First paragraph'
            }
            commentLine ' this is a comment line'
            para {
                text 'Second paragraph'
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == edoc
    }

}
