package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.parser.block.ParagraphParser

class StrongFormattingParserSpec extends AsciidogSpec {
    def parser = new ParagraphParser()

    /* === Strong Unconstrained === */
    def 'regex: single-line unconstrained strong chars'() {
        given:
        def text = '**Git**Hub'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'regex: multi-line unconstrained strong chars'() {
        given:
        def text = '**G\ni\nt\n**Hub'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong chars with inline asterisk'() {
        given:
        def text = '**bl*ck**-eye'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong chars with role'() {
        given:
        def text = 'Git[blue]**Hub**'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped unconstrained strong chars with role'() {
        given:
        def text = 'Git\\[blue]**Hub**'

        expect:
        StrongFormattingParser.STRONG_PATTERN.matcher(text).find() == true
    }

    def 'strong with attributes'() {
        given:
        def content = '[blue]*Hub*'
        def context = parserContext(content)
        context.parserId = parser.id

        def expectedPara = builder.para {
            builder.strong(attributes: ['blue': null]) {
                builder.text('Hub')
            }
        }

        when:
        def para = parser.parse(context)

        then:
        para == expectedPara
    }

}
