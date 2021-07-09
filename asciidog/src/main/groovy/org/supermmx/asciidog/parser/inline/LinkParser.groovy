package org.supermmx.asciidog.parser.inline

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.LinkNode
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.inline.InlineParserPlugin

import java.util.regex.Matcher

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Auto Link Parser
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class LinkParser extends InlineParserPlugin {
    static final TokenMatcher LINK_START_MATCHER = sequence([
        firstOf([ literal('http'), literal('https') ]),
        literal(':'),
        literal('//')
    ])

    static final String ID = 'plugin:parser:inline:link'

    LinkParser() {
        id = ID
        nodeType = Node.Type.LINK
    }

    @Override
    protected boolean doCheckStart(ParserContext context, InlineContainer parent) {
        boolean matched = LINK_START_MATCHER.matches(context, null, true)
        return matched
    }

    @Override
    protected Inline doParse(ParserContext context, InlineContainer parent) {
        LinkNode link = new LinkNode()
        link.parent = parent

        def lexer = context.lexer

        def target = lexer.joinTokensTo(firstOf([
            type(Token.Type.WHITE_SPACES),
            type(Token.Type.EOL),
            literal('[') ]), false)

        log.info '==== target = "{}", class = {}', target, target.class

        def hasBlankText = false

        def token = lexer.peek()
        if (token.value == '[') {
            lexer.next()

            token = lexer.peek()
            if (token.value == ']') {
                hasBlankText = true
            }

            // link text is specified
            context.hasLinkText = true
        } else {
            hasBlankText = true
        }

        link.target = target

        if (hasBlankText) {
            // blank text
            // set the link as the link text
            def textNode = new TextNode(target)
            textNode.parent = link
            link << textNode
        }

        return link
    }

    @Override
    protected boolean doCheckEnd(ParserContext context, InlineContainer parent) {
        if (context.hasLinkText) {
            if (literal(']').matches(context)) {
                // end
                return true
            } else {
                return false
            }
        }

        return true
    }

}
