package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.NullNode

import java.util.regex.Matcher

import groovy.util.logging.Slf4j

@Slf4j
class CriticSubstitutionParser extends AbstractCriticParser {
    CriticSubstitutionParser() {
        id = 'inline_parser_critic_substitution'
        criticType = CriticNode.CriticType.SUBSTITUTION
        pattern = ~"""(?Usxm)
\\{~~
(
  (.+)    # 2, deleted content
  ~>
  (.+)    # 3, inserted content
)
~~\\}
"""
    }

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        CriticNode substNode = new CriticNode(criticType: criticType)

        CriticNode deletionNode = new CriticNode(criticType: CriticNode.CriticType.DELETION)
        CriticNode additionNode = new CriticNode(criticType: CriticNode.CriticType.ADDITION)

        return [ substNode, deletionNode, additionNode ]
    }

    @Override
    protected boolean fillNodes(List<InlineInfo> infoList, Matcher m, List<String> groups) {
        InlineInfo substInfo = infoList[0]
        InlineInfo deletionInfo = infoList[1]
        InlineInfo additionInfo = infoList[2]

        substInfo.with {
            inlineNode.escaped = false

            fillGap = false

            start = m.start(0)
            end = m.end(0)

            contentStart = m.start(1)
            contentEnd = m.end(1)
        }

        deletionInfo.with {
            inlineNode.escaped = false

            start = m.start(2)
            end = m.end(2)

            contentStart = start
            contentEnd = end
        }

        additionInfo.with {
            inlineNode.escaped = false

            start = m.start(3)
            end = m.end(3)

            contentStart = start
            contentEnd = end
        }

        return true
    }
}
