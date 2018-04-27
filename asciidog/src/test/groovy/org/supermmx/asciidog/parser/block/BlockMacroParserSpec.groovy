package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec

class BlockMacroParserSpec extends AsciidogSpec {
    def 'document: simple macro'() {
        given:

        def content = 'image::test.jpeg[]'
        def edoc = builder.document {
            blockMacro(name: 'image', target: 'test.jpeg')
        }

        when:
        def doc = parse(content)

        then:
        doc == edoc
    }

}
