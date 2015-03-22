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

    /*
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
                             text: ' text node ',
                             start: 0, end: 11,
                             contentStart: 0, contentEnd: 11),
            builder.formattingNode(type: Node.Type.INLINE_FORMATTED_TEXT,
                                   start: 0, end: 8,
                                   contentStart: 1, contentEnd: 7,
                                   formattingType: FormattingNode.Type.STRONG) {
                current.nodes = [
                    textNode(type: Node.Type.INLINE_TEXT,
                             // FIXME: equals with traits doesn't work yet
                             text: 'abc中文',
                             start: 1, end: 7,
                             contentStart: 1, contentEnd: 7)
                ]
            }
        ]
    }
    */
}
