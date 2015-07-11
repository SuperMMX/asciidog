package org.supermmx.asciidog

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.builder.AsciiDocBuilder

import spock.lang.*

class AsciidogSpec extends Specification {
    /*
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
        builder.identifierResolver = "uid"
    }
    */

    @Shared
    def builder = new AsciiDocBuilder()

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
        def para = builder.para {
            text text
        }

        return para
    }
}
