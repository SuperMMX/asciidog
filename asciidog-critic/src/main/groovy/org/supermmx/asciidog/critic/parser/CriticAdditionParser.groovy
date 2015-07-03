package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticAdditionParser extends AbstractCriticParser {
    CriticAdditionParser() {
        id = 'inline_parser_critic_addition'
        criticType = CriticNode.CriticType.ADDITION
        pattern = ~"""(?Usxm)
\\{\\+\\+
(.+)    # 1, content
\\+\\+\\}
"""
    }
}
