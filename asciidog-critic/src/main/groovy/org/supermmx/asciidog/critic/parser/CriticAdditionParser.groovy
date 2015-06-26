package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.plugin.InlineParserPlugin

import java.util.regex.Matcher

class CriticAdditionParser extends InlineParserPlugin {
    CriticAdditionParser() {
        nodeType = CriticNode.CRITIC_NODE_TYPE
        id = 'inline_parser_critic_addition'
        pattern = ~"""(?Usxm)
\\{\\+\\+
(.+)    # 1, content
\\+\\+\\}
"""
    }

    protected Inline createNode(Matcher m, List<String> groups) {
        CriticNode criticNode = new CriticNode(criticType: CriticNode.CriticType.ADDITION)

        return criticNode
    }

    protected boolean fillNode(Inline inline, Matcher m, List<String> groups) {
        inline.info.with {
            escaped = false

            contentStart = m.start(1)
            contentEnd = m.end(1)
        }

        return true
    }
}
