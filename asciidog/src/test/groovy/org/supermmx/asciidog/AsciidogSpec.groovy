package org.supermmx.asciidog

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode

import spock.lang.*

class AsciidogSpec extends Specification {
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
        builder.identifierResolver = "uid"
    }

    /**
     * Create a parser reading from a text
     */
    def parser(def text) {
        def parser = new Parser()
        def reader = Reader.createFromString(text)
        parser.reader = reader

        return parser
    }

    /**
     * Create a simple paragraph from the text
     */
    def para(def text) {
        def length = text.length()
        def para = builder.paragraph() {
            current.info = inlineInfo(constrained: false, escaped: false,
                                      start: 0, end: length, contentStart:0, contentEnd: length)
            current.inlineNodes = [
                textNode(type: Node.Type.TEXT,
                         text: text) {
                    current.info = inlineInfo(constrained: false, escaped: false,
                                              start: 0, end: length, contentStart:0, contentEnd: length)
                }
            ]
        }

        return para
    }
}
