package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.builder.AsciiDocBuilder
import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.parser.ParserContext

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
        def lexer = new Lexer(reader)
        parser.reader = reader
        parser.lexer = lexer

        return parser
    }

    /**
     * Create a parser context reading from a text
     */
    def parserContext(def text) {
        def context = new ParserContext()
        def reader = Reader.createFromString(text)
        def lexer = new Lexer(reader)
        context.reader = reader
        context.lexer = lexer

        return context
    }

    Document parse(String text) {
        return new Parser().parseString(text)
    }
}
