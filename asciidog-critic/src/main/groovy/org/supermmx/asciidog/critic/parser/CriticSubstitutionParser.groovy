package org.supermmx.asciidog.critic.parser

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.NullNode

import java.util.regex.Matcher

import groovy.util.logging.Slf4j

@Slf4j
class CriticSubstitutionParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:substitution'

    CriticSubstitutionParser() {
        id = ID

        criticType = CriticNode.CriticType.SUBSTITUTION

        // not consume the matched tokens
        // to allow Deletion/Addition parsing for Substitution
        startMatcher = sequence([
            literal('{'),
            literal(criticType.startTag)
        ], true)
    }
}
