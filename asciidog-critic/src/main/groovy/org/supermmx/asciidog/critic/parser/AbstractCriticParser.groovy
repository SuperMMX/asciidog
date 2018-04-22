package org.supermmx.asciidog.critic.parser

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.parser.inline.InlineParserPlugin
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher

import java.util.regex.Matcher

class AbstractCriticParser extends InlineParserPlugin {
    CriticNode.CriticType criticType

    protected TokenMatcher startMatcher
    protected TokenMatcher endMatcher

    AbstractCriticParser() {
        nodeType = CriticNode.CRITIC_NODE_TYPE
    }

    @Override
    protected boolean doCheckStart(ParserContext context, InlineContainer parent) {
        if (startMatcher == null) {
            startMatcher = sequence([
                literal('{'),
                literal(criticType.startTag)
            ])
        }

        return startMatcher.matches(context)
    }

    @Override
    protected boolean doCheckEnd(ParserContext context, InlineContainer parent) {
        if (endMatcher == null) {
            endMatcher = sequence([
                literal(criticType.endTag),
                literal('}')
            ])
        }

        return endMatcher.matches(context)
    }

    @Override
    protected Inline doParse(ParserContext context, InlineContainer parent) {
        def inline = new CriticNode(criticType: criticType)
        inline.parent = parent

        return inline
    }
}
