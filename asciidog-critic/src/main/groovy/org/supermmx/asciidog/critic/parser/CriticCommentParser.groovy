package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticCommentParser extends AbstractCriticParser {
    CriticCommentParser() {
        id = 'inline_parser_critic_comment'
        criticType = CriticNode.CriticType.COMMENT
        pattern = ~"""(?Usxm)
\\{>>
(.+)    # 1, content
<<\\}
"""
    }
}
