package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class ParagraphParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:paragraph'

    ParagraphParser() {
        nodeType = Node.Type.PARAGRAPH
        id = ID
    }

    /**
     * The paragraph start matcher
     */
    static final TokenMatcher CHECK_MATCHER = sequence([
        optional(type(Token.Type.WHITE_SPACES)),
        match({ context, props, valueObj ->
            def token = context.lexer.peek()
            token.type != Token.Type.WHITE_SPACES &&
                token.type != Token.Type.EOL &&
                token.type != Token.Type.EOF
        })
    ])

    /**
     * The paragraph end matcher, which is either determined by parents
     * or by the paragraph itself (like new line after the paragraph)
     */
    static final TokenMatcher END_MATCHER = match({ context, props, valueObj ->
        // always set the current block header to null
        // as last paragraph end checking will try to set some value that may not be correct
        context.blockHeader = null

        def token = context.lexer.peek()
        log.trace '==== paragraph end matcher, next token = {}', token
        def isEnd = (token.type == Token.Type.EOL || token.type == Token.Type.EOF)
        if (isEnd) {
            return isEnd
        }

        def checkers = context.paragraphEndingCheckers
        for (def i = checkers.size() - 1; i >= 0; i--) {
            def parser = checkers[i]

            isEnd = parser.toEndParagraph(context)

            log.trace '==== paragraph to end paragraph = {}', isEnd

            // just stop here no matter what ??
            if (isEnd) {
                break
            }
        }

        return isEnd
    })

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        return CHECK_MATCHER.matches(context)
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        def para = new Paragraph()
        fillBlockFromHeader(para, header)

        context.blockHeader = null
        context.keepHeader = true

        Parser.parseInlines(context, para, END_MATCHER)

        return para
    }

    @Override
    protected boolean doNeedToFindNextChildParser(ParserContext context) {
        return false
    }
}
