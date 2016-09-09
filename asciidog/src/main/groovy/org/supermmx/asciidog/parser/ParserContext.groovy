package org.supermmx.asciidog.parser

import org.supermmx.asciidog.AttributeContainer
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

class ParserContext {
    Reader reader

    AttributeContainer attributes = new AttributeContainer()

    Document document

    Stack<Block> parents = []
    Stack<BlockParserPlugin> parentParsers = []

    Node currentNode
    BlockHeader blockHeader

    Block getParent() {
        return parents.peek()
    }

    BlockParserPlugin getParentParser() {
        return parentParsers.peek()
    }
}
