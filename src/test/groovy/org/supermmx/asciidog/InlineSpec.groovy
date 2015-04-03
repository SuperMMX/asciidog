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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 0, end: 7, contentStart: 2, contentEnd: 5)
                current.inlineNodes = [
                    new TextNode('Git', 2)
                ]
            },
            new TextNode('Hub', 7)
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'escaped single-line unconstrained strong chars'() {
        given:
        def text = '\\**Git**Hub'
        def nodes = [
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:true,
                                          start: 0, end: 8, contentStart: 3, contentEnd: 6)
                current.inlineNodes = [
                    new TextNode('Git', 3)
                ]
            },
            new TextNode('Hub', 8)
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'multi-line unconstrained strong chars'() {
        given:
        def text = '**G\ni\nt\n**Hub'
        def nodes = [
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 0, end: 10, contentStart: 2, contentEnd: 8)
                current.inlineNodes = [
                    new TextNode('G\ni\nt\n', 2)
                ]
            },
            new TextNode('Hub', 10)
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'unconstrained strong chars with inline asterisk'() {
        given:
        def text = '**bl*ck**-eye'
        def nodes = [
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 0, end: 9, contentStart: 2, contentEnd: 7)
                current.inlineNodes = [
                    new TextNode('bl*ck', 2)
                ]
            },
            new TextNode('-eye', 9)
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'unconstrained strong chars with role'() {
        given:
        def text = 'Git[blue]**Hub**'
        def nodes = [
            new TextNode('Git', 0),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG,
                                   attributes: ['blue': null]) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 3, end: 16, contentStart: 11, contentEnd: 14)
                current.inlineNodes = [
                    new TextNode('Hub', 11)
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
            new TextNode('中', 0),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 1, end: 7, contentStart: 3, contentEnd: 5)
                current.inlineNodes = [
                    new TextNode('文段', 3)
                ]
            },
            new TextNode('落 with o', 7),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 15, end: 21, contentStart: 17, contentEnd: 19)
                current.inlineNodes = [
                    new TextNode('th', 17)
                ]
            },
            new TextNode('er words\n', 21),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 30, end: 35, contentStart: 31, contentEnd: 34)
                current.inlineNodes = [
                    new TextNode('abc', 31)
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
            new TextNode('Git', 0),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG,
                                   attributes: ['blue': null]) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 3, end: 16, contentStart: 11, contentEnd: 14)
                current.inlineNodes = [
                    new TextNode('Hub', 11)
                ]
            }
        ]

        // FIXME: escaped
    }

    def 'escaped unconstrained strong chars with role'() {
        given:
        def text = 'Git\\[blue]**Hub**'
        def nodes = [
            new TextNode('Git', 0),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG,
                                   attributes: ['blue': null]) {
                current.info = inlineInfo(constrained: false, escaped:false,
                                          start: 3, end: 16, contentStart: 11, contentEnd: 14)
                current.inlineNodes = [
                    new TextNode('Hub', 11)
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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: text.length(), contentStart: 1, contentEnd: text.length() - 1)
                current.inlineNodes = [
                    new TextNode(content, 1)
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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: text.length(), contentStart: 1, contentEnd: text.length() - 1)
                current.inlineNodes = [
                    new TextNode(content, 1)
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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: text.length(), contentStart: 1, contentEnd: text.length() - 1)
                current.inlineNodes = [
                    new TextNode(content, 1)
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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: 7, contentStart: 1, contentEnd: 6)
                current.inlineNodes = [
                    new TextNode('bl*ck', 1)
                ]
            },
            new TextNode('-eye', 7)
        ]

        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == nodes
    }

    def 'constrained strong string containing an asterisk and multibyte word chars'() {
        given:
        def text = '*黑*眼圈*'
        def nodes = [
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: 6, contentStart: 1, contentEnd: 5)
                current.inlineNodes = [
                    new TextNode('黑*眼圈', 1)
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
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG,
                                   attributes: [ 'blue':null ]) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: 26, contentStart: 7, contentEnd: 25)
                current.inlineNodes = [
                    new TextNode('a few strong words', 7)
                ]
            }
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
            new TextNode(' text node ', 0),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: 7, contentStart: 1, contentEnd: 6)

                current.inlineNodes = [
                    new TextNode('abc中文', 1)
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
