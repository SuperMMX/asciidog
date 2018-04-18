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

    def 'single-line unconstrained strong chars'() {
        given:
        def text = '**Git**Hub'
        def nodes = [
            builder.strong {
                builder.text('Git')
            },
            builder.text('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'
        def nodes = [
            builder.strong(escaped: true) {
                builder.text('Git')
            },
            builder.text('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'multi-line unconstrained strong chars'() {
        given:
        def text = '**G\ni\nt\n**Hub'
        def nodes = [
            builder.strong {
                builder.text('G\ni\nt\n')
            },
            builder.text('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'unconstrained strong chars with inline asterisk'() {
        given:
        def text = '**bl*ck**-eye'
        def nodes = [
            builder.strong {
                builder.text('bl*ck')
            },
            builder.text('-eye')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    @Ignore
    def 'unconstrained strong chars with role'() {
        given:
        def text = 'Git[blue]**Hub**'
        def nodes = [
            builder.text('Git'),
            builder.strong(attributes: ['blue': null]) {
                builder.text('Hub')
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    @Ignore
    def 'mixed unconstrained and constrained strong inlines'() {
        given:
        def text = '中**文段**落 with o**th**er words\n*abc*'
        def nodes = [
            builder.text('中'),
            builder.strong {
                builder.text('文段')
            },
            builder.text('落 with o'),
            builder.strong {
                builder.text('th')
            },
            builder.text('er words\n'),
            builder.strong {
                builder.text('abc')
            },
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped unconstrained strong chars with role'() {
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

    /* === Strong Constrained === */
    def 'single-line constrained strong string'() {
        given:
        def content = 'a few strong words'
        def text = "*$content*"
        def nodes = [
            builder.strong {
                builder.text(content)
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped single-line constrained strong string'() {
        given:
        def content = 'a few strong words'
        def text = "\\*$content*"
        def nodes = [
            builder.strong(escaped: true) {
                builder.text(content)
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'multi-line constrained strong string'() {
        given:
        def content = 'a few\nstrong words'
        def text = "*$content*"
        def nodes = [
            builder.strong {
                builder.text(content)
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string containing an asterisk'() {
        given:
        def text = '*bl*ck*-eye'
        def nodes = [
            builder.strong {
                builder.text('bl*ck')
            },
            builder.text('-eye')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string containing an asterisk and multibyte word chars'() {
        given:
        def text = '*黑*眼圈*'
        def nodes = [
            builder.strong {
                builder.text('黑*眼圈')
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string with role'() {
        given:
        def text = '[blue]*a few strong words*'
        def nodes = [
            builder.strong(attributes: [ 'blue':null ]) {
                builder.text('a few strong words')
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
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

    def 'simple nodes'() {
        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == [ node ]

        where:
        text << [
            ' text node ',
            '*abc中文*'
        ]

        node << [
            builder.text(' text node '),
            builder.strong {
                builder.text('abc中文')
            }
        ]
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
