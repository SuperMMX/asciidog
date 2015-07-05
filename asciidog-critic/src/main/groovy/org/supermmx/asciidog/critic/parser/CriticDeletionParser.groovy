package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticDeletionParser extends AbstractCriticParser {
    CriticDeletionParser() {
        id = 'inline_parser_critic_deletion'
        criticType = CriticNode.CriticType.DELETION
        pattern = ~"""(?Usxm)
\\{--
(.+)    # 1, content
--\\}
"""
    }
}
