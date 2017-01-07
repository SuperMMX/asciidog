package org.supermmx.asciidog.critic.parser

import org.supermmx.asciidog.critic.CriticNode

class CriticCommentParser extends AbstractCriticParser {
    static final String ID = 'plugin:parser:inline:critic:comment'

    CriticCommentParser() {
        id = ID

        criticType = CriticNode.CriticType.COMMENT
        pattern = ~"""(?Usxm)
\\{>>
(.+)    # 1, content
<<\\}
"""
    }
}
