package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.parser.block.ParagraphParser

import spock.lang.*

class InlineSpec extends AsciidogSpec {
    def parser = new ParagraphParser()

    /* === Node: Strong Unconstrained === */

    def 'single-line strong chars'() {
        given:
        def context = parserContext('**Git**Hub')
        context.parserId = parser.id
        def ePara = builder.para {
            strong {
                text 'Git'
            }
            text 'Hub'
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'multi-line strong chars'() {
        given:
        def context = parserContext('**G\ni\nt\n**Hub')
        context.parserId = parser.id
        def ePara = builder.para {
            strong {
                text 'G\ni\nt\n'
            }
            text 'Hub'
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'unconstrained strong chars with role'() {
        given:
        def context = parserContext('Git[blue]**Hub**')
        context.parserId = parser.id
        def ePara = builder.para {
            text 'Git'
            builder.strong(attributes: ['blue': null]) {
                text 'Hub'
            }
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'strong chars with role'() {
        given:
        def context = parserContext('Git[blue]*Hub*')
        context.parserId = parser.id
        def ePara = builder.para {
            text 'Git'
            builder.strong(attributes: ['blue': null]) {
                text 'Hub'
            }
        }

        expect:
        parser.parse(context) == ePara
    }

    @Ignore
    def 'escaped strong chars with role'() {
        given:
        def text = 'Git\\[blue]**Hub**'
        def nodes = [
            builder.text('Git'),
            builder.strong(attributes: ['blue': null]) {
                builder.text('Hub')
            }
        ]

        // FIXME: escaped
    }

    /* ==== Cross Reference ==== */
    def 'xref using angled bracket syntax'() {
        given:
        def context = parserContext('<<tigers>>')
        context.parserId = parser.id
        def ePara = builder.para {
            xref 'tigers'
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'xref cut in the middle of strong'() {
        given:
        def context = parserContext('**strong with <<xref** node>>')
        context.parserId = parser.id
        def ePara = builder.para {
            strong {
                text 'strong with '
                xref 'xref** node'
            }
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'strong cut in the middle of xref'() {
        given:
        def context = parserContext('<<xref **node>> in strong** words')
        context.parserId = parser.id
        def ePara = builder.para {
            xref 'xref **node'
            text ' in strong'
            strong {
                text ' words'
            }
        }

        expect:
        parser.parse(context) == ePara
    }

    /* === Node: Mark === */

    def 'mark with role'() {
        given:
        def context = parserContext('Git[blue]#Hub#')
        context.parserId = parser.id
        def ePara = builder.para {
            text 'Git'
            builder.mark(attributes: ['blue': null]) {
                text 'Hub'
            }
        }

        expect:
        parser.parse(context) == ePara
    }

    def 'unconstrained mark with role'() {
        given:
        def context = parserContext('Git[blue]##Hub##')
        context.parserId = parser.id
        def ePara = builder.para {
            text 'Git'
            builder.mark(attributes: ['blue': null]) {
                text 'Hub'
            }
        }

        expect:
        parser.parse(context) == ePara
    }


    def 'simple nodes'() {
        given:
        def context = parserContext(' text node ')
        context.parserId = parser.id
        def ePara = builder.para {
            text 'text node '
        }

        expect:
        parser.parse(context) == ePara

        when:
        context = parserContext(' **abc 中文** ')
        context.parserId = parser.id
        ePara = builder.para {
            strong {
                text 'abc 中文'
            }
            text ' '
        }

        then:
        parser.parse(context) == ePara

    }

    def 'test'() {
        given:
            def context = parserContext('''
中**文段落 this is a ch__ine__se **paragraph
多__行段落__ the end''')
        context.parserId = parser.id

        def ePara = builder.para {
            text '中'
            strong {
                text '文段落 this is a ch'
                em {
                    text 'ine'
                }
                text 'se '
            }
            text 'paragraph\n多'
            em {
                text '行段落'
            }
            text ' the end'
        }

        expect:
        parser.parse(context) == ePara
    }
}
