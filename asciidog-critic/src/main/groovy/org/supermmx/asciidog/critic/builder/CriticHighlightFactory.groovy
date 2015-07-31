package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

class CriticHighlightFactory extends AbstractCriticNodeFactory {
    CriticHighlightFactory() {
        name = 'criticHighlight'
        criticType = CriticNode.CriticType.HIGHLIGHT
    }
}

