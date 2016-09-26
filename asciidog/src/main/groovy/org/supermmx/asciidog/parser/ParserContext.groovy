package org.supermmx.asciidog.parser

import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.Context
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

class ParserContext implements Context {
    Reader reader

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
     * Whether to pass the header to child after creating the block
     */
    // keepHeader

    /**
     * Whether this parser is spected by the parent parser, normally
     * this property is set into context.childParserProps in parent's
     * getNextChildParser()
     */
    //expected

    // other custom properties
}
