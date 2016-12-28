package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.parser.inline.InlineParserPlugin

import java.util.regex.Matcher

class AbstractCriticParser extends InlineParserPlugin {
    CriticNode.CriticType criticType

    AbstractCriticParser() {
        nodeType = CriticNode.CRITIC_NODE_TYPE
    }

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        CriticNode criticNode = new CriticNode(criticType: criticType)

        return [ criticNode ]
    }

    @Override
    protected boolean fillNodes(List<InlineInfo> infoList, Matcher m, List<String> groups) {
        infoList[0].with {
            inlineNode.escaped = false

            contentStart = m.start(1)
            contentEnd = m.end(1)
        }

        return true
    }
}
