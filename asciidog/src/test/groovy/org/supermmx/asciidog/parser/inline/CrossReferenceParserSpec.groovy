package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.block.ParagraphParser

class CrossReferenceParserSpec extends AsciidogSpec {
    def parser = new ParagraphParser()

    def 'xref using angled bracket syntax'() {
        given:
        def content = '<<tigers>>'
        def context = parserContext(content)
        context.parserId = parser.id

        def expectedPara = builder.para {
            xref 'tigers'
        }

        when:
        def para = parser.parse(context)

        then:
        para == expectedPara
    }

    def 'xref with undercore and space'() {
        given:
        def content = '<<_cross ref>>'
        def context = parserContext(content)
        context.parserId = parser.id

        def expectedPara = builder.para {
            xref '_cross ref'
        }

        when:
        def para = parser.parse(context)

        then:
        para == expectedPara
    }

    def 'xref start with colon'() {
        given:
        def content = '<<:title>>'
        def context = parserContext(content)
        context.parserId = parser.id

        def expectedPara = builder.para {
            xref ':title'
        }


        when:
        def para = parser.parse(context)

        then:
        para == expectedPara
    }
}
