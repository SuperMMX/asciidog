package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode

import spock.lang.*

class InlineSpec extends AsciidogSpec {
    /* === Strong Unconstrained === */
    def 'regex: single-line unconstrained strong chars'() {
        given:
        def text = '**Git**Hub'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: multi-line unconstrained strong chars'() {
        given:
        def text = '**G\ni\nt\n**Hub'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong chars with inline asterisk'() {
        given:
        def text = '**bl*ck**-eye'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong chars with role'() {
        given:
        def text = 'Git[blue]**Hub**'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped unconstrained strong chars with role'() {
        given:
        def text = 'Git\\[blue]**Hub**'

        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == true
    }

    /* === Strong Constrained === */
    def 'regex: single-line constrained strong string'() {
        given:
        def text = '*a few strong words*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped single-line constrained strong string'() {
        given:
        def text = '\\*a few strong words*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: multi-line constrained strong string'() {
        given:
        def text = '*a few\nstrong words*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: constrained strong string containing an asterisk'() {
        given:
        def text = '*bl*ck*-eye'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: unconstrained strong string should not match constrained strong'() {
        given:
        def text = '**bl*ck**-eye'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == false
    }

    def 'regex: constrained strong string containing an asterisk and multibyte word chars'() {
        given:
        def text = '*黑*眼圈*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: constrained strong string with role'() {
        given:
        def text = '[blue]*a few strong words*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

    def 'regex: escaped constrained strong string with role'() {
        given:
        def text = '\\[blue]*a few strong words*'

        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == true
    }

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
        def text = '<<tigers>>'
        def nodes = [
            builder.xref('tigers')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'xref cut in the middle of strong'() {
        given:
        def text = '*strong with <<xref* node>>'
        def nodes = [
            builder.strong {
                builder.text('strong with <<xref')
            },
            builder.text(' node>>')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'strong cut in the middle of xref'() {
        given:
        def text = '<<xref *node>> in *strong words*'
        def nodes = [
            builder.xref('xref *node'),
            builder.text(' in '),
            builder.strong {
                builder.text('strong words')
            }
        ]

        // FIXME: need to re-think the inline parsing
        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
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
        Parser.parseInlineNodes(new Paragraph(),
                               '中**文段**落 this is a ch**ine**se paragraph\n*多行段落*')
    }
}
