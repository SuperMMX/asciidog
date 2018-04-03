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
        match({ context, header, valueObj ->
            def token = context.lexer.next()
            token.type != Token.Type.WHITE_SPACES &&
                token.type != Token.Type.EOL &&
                token.type != Token.Type.EOF
        })
    ])

    /**
     * The paragraph end matcher, which is either determined by parents
     * or by the paragraph itself (like new line after the paragraph)
     */
    static final TokenMatcher END_MATCHER = match({ context, header, valueObj ->
        def token = context.lexer.peek()
        def isEnd = (token.type == Token.Type.EOL || token.type == Token.Type.EOF)
        if (isEnd) {
            return isEnd
        }

        def checkers = context.paragraphEndingCheckers
        for (def i = checkers.size() - 1; i >= 0; i--) {
            def parser = checkers[i]

            isEnd = parser.toEndParagraph(context)

            log.debug '==== paragraph to end paragraph = {}', isEnd
            // just stop here no matter what ??
            if (isEnd) {
                break
            }
        }

        return isEnd
    })

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        return CHECK_MATCHER.matches(context, header)
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        def para = new Paragraph()
        fillBlockFromHeader(para, header)

        context.blockHeader = null
        context.keepHeader = true

        Parser.parseInlines(context, para, END_MATCHER)

        if (true) {
            return para
        }

        // old code
        para = null

        def lines = []

        def buf = new StringBuilder()
        while (lexer.hasNext()) {
            if (para == null) {
                para = new Paragraph()
                fillBlockFromHeader(para, header)

                context.blockHeader = null
                context.keepHeader = true
            }

            def token = lexer.peek()
            log.trace '==== paragraph token = {}', token
            if (token.type == Token.Type.EOF) {
                break
            }

            lexer.next()
            if (token.type != Token.Type.EOL) {
                log.trace '==== paragraph append to line: {}', token
                buf.append(token.value)
            } else {
                if (buf.length() == 0) {
                    // blank in a new line
                    context.blockHeader = null
                    break
                } else {
                    // end of current line
                    def line = buf.toString()
                    log.debug '==== paragraph add new line: {}', line
                    lines << line

                    buf = new StringBuilder()

                    // paragraph ends
                    if (lexer.peek().type == Token.Type.EOL) {
                        break
                    }

                    def isEnd = false
                    def checkers = context.paragraphEndingCheckers
                    for (def i = checkers.size() - 1; i >= 0; i--) {
                        def parser = checkers[i]

                        isEnd = parser.toEndParagraph(context)

                        log.debug '==== paragraph to end paragraph = {}', isEnd
                        // just stop here no matter what ??
                        if (isEnd) {
                            break
                        }
                    }

                    if (isEnd) {
                        break
                    } else {
                        if (context.blockHeader?.lines) {
                            lines.addAll(context.blockHeader?.lines)
                        }
                        context.blockHeader = null
                    }

                }
            }
        }

        if (para != null) {
            // parse the inline nodes
            // the children has been added in the paragraph when parsing
            log.debug '==== paragraph lines = {}, next token = {}', lines, lexer.peek()
            Parser.parseInlineNodes(para, lines.join('\n'))
        }

        log.debug('End parsing paragraph, parent type: {}', parent?.type)

        return para
    }
}
