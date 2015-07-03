package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.plugin.InlineParserPlugin

import java.util.regex.Matcher

class AbstractCriticParser extends InlineParserPlugin {
    CriticNode.CriticType criticType

    AbstractCriticParser() {
        nodeType = CriticNode.CRITIC_NODE_TYPE
    }

    @Override
    protected Inline createNode(Matcher m, List<String> groups, InlineInfo info) {
        CriticNode criticNode = new CriticNode(criticType: criticType)

        return criticNode
    }

    @Override
    protected boolean fillNode(Inline inline, Matcher m, List<String> groups, InlineInfo info) {
        inline.escaped = false

        info.with {
            contentStart = m.start(1)
            contentEnd = m.end(1)
        }

        return true
    }
}
