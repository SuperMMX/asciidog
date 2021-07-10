package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.CommentLine
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.DataNode
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * The block macro parser.
 * name::target[attributes]
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class CommentLineParser extends BlockParserPlugin {
    static final TokenMatcher START_MATCHER = literal('//')

    static final String ID = 'plugin:parser:block:comment_line'

    CommentLineParser() {
        nodeType = Node.Type.COMMENT_LINE
        id = ID
    }

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        def lexer = context.lexer

        def isStart = START_MATCHER.matches(context, null, false)

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        CommentLine commentLine = new CommentLine()
        commentLine.parent = parent

        def text = context.lexer.joinTokensTo(type(Token.Type.EOL))

        def dataNode = new DataNode(text)
        dataNode.parent = commentLine

        commentLine << dataNode

        return commentLine
    }

    protected boolean doNeedToFindNextChildParser(ParserContext context) {
        return false
    }

}
