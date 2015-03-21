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

    def 'regex: strong constrained'() {
        expect:
        Parser.STRONG_CONSTRAINED_PATTERN.matcher(text).find() == result

        where:
        text << [
            // positive
            '*abc*',
            ': *abc* def',
            '*中文*',
            '：*中文*。',
            // escape
            '\\*abc*',
            ': \\*abc* def',
            '\\*中文*',
            '：\\*中文*。',
            // multi-line
            '*ab\nc*',
            'def *ab\ncd* def',
            '*中\n文*',
            '  *中\n文*。',
            
            // negative
            'a*b*c',
            '中*文*中文'
        ]

        result << [
            true, true, true, true,
            true, true, true, true,
            true, true, true, true,
            false, false
        ]
    }

    def 'regex: strong unconstrained'() {
        expect:
        Parser.STRONG_UNCONSTRAINED_PATTERN.matcher(text).find() == result

        where:
        text << [
            // positive
            '**abc**',
            '**中文**',
            'ab**cd**ef',
            '中**文支**持',
            // escape
            '\\**abc**',
            '\\**中文**',
            // multi-line
            '**ab\ncd\n**ef',
            '**中\n文\n**支持',
        ]

        result << [
            true, true, true, true,
            true, true,
            true, true
        ]
    }

    def 'regex: emphasis constrained'() {
        expect:
        Parser.EMPHASIS_CONSTRAINED_PATTERN.matcher(text).find() == result

        where:
        text << [
            // positive
            '_abc_',
            ': _abc_ def',
            '_中文_',
            '：_中文_。',
            // escape
            '\\_abc_',
            ': \\_abc_ def',
            '\\_中文_',
            '：\\_中文_。',
            // multi-line
            '_ab\nc_',
            'def _ab\ncd_ def',
            '_中\n文_',
            '  _中\n文_。',
            
            // negative
            'a_b_c',
            '中_文_中文'
        ]

        result << [
            true, true, true, true,
            true, true, true, true,
            true, true, true, true,
            false, false
        ]
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
}
