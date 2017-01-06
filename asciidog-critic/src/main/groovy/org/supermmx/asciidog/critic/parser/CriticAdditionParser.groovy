package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticAdditionParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:addition'

    CriticAdditionParser() {
        id = ID
        criticType = CriticNode.CriticType.ADDITION
        pattern = ~"""(?Usxm)
\\{\\+\\+
(.+)    # 1, content
\\+\\+\\}
"""
    }
}
