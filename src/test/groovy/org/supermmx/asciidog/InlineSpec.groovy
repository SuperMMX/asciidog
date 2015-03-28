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

class InlineSpec extends Specification {
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
        builder.identifierResolver = "uid"
    }

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

    def 'simple nodes'() {
        expect:
        Parser.parseInlineNodes(new Paragraph(), text) == [ node ]

        where:
        text << [
            ' text node ',
            '*abc中文*'
        ]

        node << [
            builder.textNode(type: Node.Type.INLINE_TEXT,
                             text: ' text node ') {
                current.info = inlineInfo(constrained: false, escaped: false,
                                          start: 0, end: 11, contentStart: 0, contentEnd: 11)
            },

            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.info = inlineInfo(constrained: true, escaped:false,
                                          start: 0, end: 7, contentStart: 1, contentEnd: 6)

                current.inlineNodes = [
                    textNode(type: Node.Type.INLINE_TEXT,
                             text: 'abc中文') {
                        current.info = inlineInfo(constrained: false, escaped: false,
                                                  start: 1, end: 6, contentStart: 1, contentEnd: 6)
                    }
                ]
            }
        ]
    }
}
