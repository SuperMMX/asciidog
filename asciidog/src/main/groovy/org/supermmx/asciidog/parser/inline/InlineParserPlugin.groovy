package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.ParserPlugin
import org.supermmx.asciidog.parser.TokenMatcher

import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Inline parser plugin that parses the inline nodes
 * it is interested in
 */
abstract class InlineParserPlugin extends ParserPlugin {
    /**
     * [style, key="value" new-key='new value' ]
     */
    static final def INLINE_ATTRIBUTES_PATTERN = ~'''(?x)
\\[                   # start with [
(                     # 1, atrribute line
  \\p{Blank}*
  [\\w{},.\\#"'%].*   # '
)
\\]                   # end with ]
'''

    boolean checkStart(ParserContext context, InlineContainer parent) {
        // parse attributes
        def lexer = context.lexer
        def token = lexer.peek()
        if (token.type == Token.Type.PUNCTS
            && token.value == '[') {
            lexer.next()
            def attributesText = lexer.joinTokensTo(TokenMatcher.literal(']'));

            context.nodeAttributes = parseAttributes(attributesText)
        }

        return doCheckStart(context, parent)
    }

    abstract protected boolean doCheckStart(ParserContext context, InlineContainer parent)

    boolean checkEnd(ParserContext context, InlineContainer parent) {
        return doCheckEnd(context, parent)
    }

    abstract protected boolean doCheckEnd(ParserContext context, InlineContainer parent)

    public Inline parse(ParserContext context, InlineContainer parent) {
        def inlineNode = doParse(context, parent)
        if (context.nodeAttributes != null) {
            inlineNode.attributes.putAll(context.nodeAttributes)
        }

        return inlineNode
    }

    abstract protected Inline doParse(ParserContext context, InlineContainer parent)
}
