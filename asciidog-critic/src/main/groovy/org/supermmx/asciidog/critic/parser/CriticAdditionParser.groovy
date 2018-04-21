package org.supermmx.asciidog.critic.parser

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class CriticAdditionParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:addition'

    static final TokenMatcher START_MATCHER_FOR_SUBSTITUTION = sequence([
        literal('~'),
        literal('>')
    ])

    static final TokenMatcher END_MATCHER_FOR_SUBSTITUTION = sequence([
        literal(CriticNode.TAG_SUBSTITUTION),
        literal('}')
    ], true)

    CriticAdditionParser() {
        id = ID
        criticType = CriticNode.CriticType.ADDITION
    }

    @Override
    protected boolean doCheckStart(ParserContext context, InlineContainer parent) {
        def result = false

        if (parent in CriticNode
            && parent.criticType == CriticNode.CriticType.SUBSTITUTION) {
            result = START_MATCHER_FOR_SUBSTITUTION.matches(context)
        } else {
            result = super.doCheckStart(context, parent)
        }

        return result
    }

    @Override
    protected boolean doCheckEnd(ParserContext context, InlineContainer parent) {
        def result = false

        if (parent in CriticNode
            && parent.criticType == CriticNode.CriticType.SUBSTITUTION) {
            result = END_MATCHER_FOR_SUBSTITUTION.matches(context)
        } else {
            result = super.doCheckEnd(context, parent)
        }

        return result
    }

}
