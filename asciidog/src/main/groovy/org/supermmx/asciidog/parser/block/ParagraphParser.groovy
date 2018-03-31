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

    static final TokenMatcher MATCHER = sequence([
        optional(type(Token.Type.WHITE_SPACES)),
        match({ token, valueObj ->
            token.type != Token.Type.WHITE_SPACES &&
                token.type != Token.Type.EOL &&
                token.type != Token.Type.EOF
        })
    ])

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        return MATCHER.matches(context, header)
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        Paragraph para = null

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
            log.info '==== paragraph token = {}', token
            if (token.type == Token.Type.EOF) {
                break
            }

            lexer.next()
            if (token.type != Token.Type.EOL) {
                log.info '==== paragraph append to line: {}', token
                buf.append(token.value)
            } else {
                if (buf.length() == 0) {
                    // blank in a new line
                    context.blockHeader = null
                    break
                } else {
                    // end of current line
                    def line = buf.toString()
                    log.info '==== paragraph add new line: {}', line
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

                        log.info '==== paragraph to end paragraph = {}', isEnd
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
            log.info '==== paragraph lines = {}, next token = {}', lines, lexer.peek()
            Parser.parseInlineNodes(para, lines.join('\n'))
        }

        log.debug('End parsing paragraph, parent type: {}', parent?.type)

        return para
    }
}
