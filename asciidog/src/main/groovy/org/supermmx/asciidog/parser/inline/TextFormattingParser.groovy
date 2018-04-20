package org.supermmx.asciidog.parser.inline

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.inline.InlineParserPlugin

import java.util.regex.Matcher

abstract class TextFormattingParser extends InlineParserPlugin {
    String tag
    TokenMatcher matcher

    @Override
    protected boolean doCheckStart(ParserContext context) {
        if (tag == null) {
            return false
        }
        if (matcher == null) {
            matcher = literal(tag)
        }
        return matcher.matches(context)
    }

    @Override
    protected boolean doCheckEnd(ParserContext context) {
        if (tag == null) {
            return false
        }
        if (matcher == null) {
            matcher = literal(tag)
        }
        return matcher.matches(context)
    }

    @Override
    protected Inline doParse(ParserContext context, InlineContainer parent) {
        def inline = createFormattingNode()
        inline.parent = parent

        return inline
    }
}
