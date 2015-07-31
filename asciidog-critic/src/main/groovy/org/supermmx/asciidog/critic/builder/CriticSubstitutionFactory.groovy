package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

class CriticSubstitutionFactory extends AbstractCriticNodeFactory {
    CriticSubstitutionFactory() {
        name = 'criticSubst'
        criticType = CriticNode.CriticType.SUBSTITUTION
    }
}

