package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

class CriticCommentFactory extends AbstractCriticNodeFactory {
    CriticCommentFactory() {
        name = 'criticComment'
        criticType = CriticNode.CriticType.COMMENT
    }
}

