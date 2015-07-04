package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticHighlightParser extends AbstractCriticParser {
    CriticHighlightParser() {
        id = 'inline_parser_critic_highlight'
        criticType = CriticNode.CriticType.HIGHLIGHT
        pattern = ~"""(?Usxm)
\\{==
(.+)    # 1, content
==\\}
"""
    }
}
