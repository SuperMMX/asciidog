package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticHighlightParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:highlight'

    CriticHighlightParser() {
        id = ID

        criticType = CriticNode.CriticType.HIGHLIGHT
        pattern = ~"""(?Usxm)
\\{==
(.+)    # 1, content
==\\}
"""
    }
}
