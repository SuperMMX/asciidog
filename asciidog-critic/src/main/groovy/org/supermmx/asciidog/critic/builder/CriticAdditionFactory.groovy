package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

class CriticAdditionFactory extends AbstractCriticNodeFactory {
    CriticAdditionFactory() {
        name = 'criticAdd'
        criticType = CriticNode.CriticType.ADDITION
    }
}

