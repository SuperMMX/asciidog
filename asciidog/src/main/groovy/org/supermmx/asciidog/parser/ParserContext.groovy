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

    Node currentNode
    BlockHeader blockHeader

    // parent
    // block
    // parser
    // other custom properties
}
