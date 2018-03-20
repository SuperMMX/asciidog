package org.supermmx.asciidog.parser

import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.Context
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader
import org.supermmx.asciidog.parser.block.DocumentParser
import org.supermmx.asciidog.lexer.Lexer

class ParserContext implements Context {
    Reader reader
    Lexer lexer

    AttributeContainer attributes = new AttributeContainer()

    Document document

    /**
     * The block header for current parser
     */
    BlockHeader blockHeader

    /**
     * The properties set by getNextChildParser and will be copied
     * to the context properties when the child parser is parsing
     */
    def childParserProps = [:]

    /**
     * The parent parsers that will check whether a line indicates
     * the end of the current paragraph
     */
    def paragraphEndingCheckers = []

    /**
     * Whether to stop the parsing immediately
     */
    def stop = false

    ParserContext() {
        properties['parserId'] = DocumentParser.ID
    }

    /**
     * The parent block
     */
    // parent

    /**
     * The current block
     */
    // block

    /**
     * The child parser ID
     */
    // parserId

    /**
     * How many time this parser is called
     */
    // parserCallingCount

    /**
     * The starting index of expected parsing blocks
     */
    // parserStartIndex

    /**
     * Whether to pass the header to child after creating the block
     */
    // keepHeader

    /**
     * Whether this parser is spected by the parent parser, normally
     * this property is set into context.childParserProps in parent's
     * getNextChildParser()
     */
    //expected

    /**
     * To pass some properties from child parser to the parent parser
     */
    // parentParserProps

    // other custom properties
}
