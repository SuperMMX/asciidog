package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticDeletionParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:delete'

    CriticDeletionParser() {
        id = ID

        criticType = CriticNode.CriticType.DELETION
        pattern = ~"""(?Usxm)
\\{--
(.+)    # 1, content
--\\}
"""
    }
}
