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
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('Git')
                ]
            },
            new TextNode('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG,
                                   escaped: true) {
                current.inlineNodes = [
                    new TextNode('Git')
                ]
            },
            new TextNode('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'multi-line unconstrained strong chars'() {
        given:
        def text = '**G\ni\nt\n**Hub'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('G\ni\nt\n')
                ]
            },
            new TextNode('Hub')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'unconstrained strong chars with inline asterisk'() {
        given:
        def text = '**bl*ck**-eye'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('bl*ck')
                ]
            },
            new TextNode('-eye')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'unconstrained strong chars with role'() {
        given:
        def text = 'Git[blue]**Hub**'
        def nodes = [
            new TextNode('Git'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG,
                                   attributes: ['blue': null]) {
                current.inlineNodes = [
                    new TextNode('Hub')
                ]
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'mixed unconstrained and constrained strong inlines'() {
        given:
        def text = '中**文段**落 with o**th**er words\n*abc*'
        def nodes = [
            new TextNode('中'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('文段')
                ]
            },
            new TextNode('落 with o'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('th')
                ]
            },
            new TextNode('er words\n'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('abc')
                ]
            },
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'
        def nodes = [
            new TextNode('Git'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG,
                                   attributes: ['blue': null]) {
                current.inlineNodes = [
                    new TextNode('Hub')
                ]
            }
        ]

        // FIXME: escaped
    }

    def 'escaped unconstrained strong chars with role'() {
        given:
        def text = 'Git\\[blue]**Hub**'
        def nodes = [
            new TextNode('Git'),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG,
                                   attributes: ['blue': null]) {
                current.inlineNodes = [
                    new TextNode('Hub')
                ]
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
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode(content)
                ]
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
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode(content)
                ]
            }
        ]

        // FIXME
        //expect:
        //Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'multi-line constrained strong string'() {
        given:
        def content = 'a few\nstrong words'
        def text = "*$content*"
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode(content)
                ]
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string containing an asterisk'() {
        given:
        def text = '*bl*ck*-eye'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('bl*ck')
                ]
            },
            new TextNode('-eye')
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string containing an asterisk and multibyte word chars'() {
        given:
        def text = '*黑*眼圈*'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('黑*眼圈')
                ]
            }
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string with role'() {
        given:
        def text = '[blue]*a few strong words*'
        def nodes = [
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG,
                                   attributes: [ 'blue':null ]) {
                current.inlineNodes = [
                    new TextNode('a few strong words')
                ]
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
            builder.crossReferenceNode(type: Node.Type.CROSS_REFERENCE,
                                       xrefId: 'tigers')
        ]

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
            new TextNode(' text node '),
            builder.formattingNode(type: Node.Type.FORMATTING,
                                   formattingType: FormattingNode.FormattingType.STRONG) {
                current.inlineNodes = [
                    new TextNode('abc中文')
                ]
            }
        ]
    }

    def 'test'() {
        given:
        Parser.parseInlineNodes(new Paragraph(),
                               '中**文段**落 this is a ch**ine**se paragraph\n*多行段落*')
    }
}
