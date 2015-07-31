package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

class CriticDeletionFactory extends AbstractCriticNodeFactory {
    CriticDeletionFactory() {
        name = 'criticDelete'
        criticType = CriticNode.CriticType.DELETION
    }
}

