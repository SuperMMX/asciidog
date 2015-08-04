package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode
import org.supermmx.asciidog.plugin.BuilderPlugin

class CriticBuilderPlugin extends BuilderPlugin {
    CriticBuilderPlugin() {
        id = 'builder_critic'
        nodeType = CriticNode.CRITIC_NODE_TYPE

        factories << new CriticAdditionFactory()
        factories << new CriticDeletionFactory()
        factories << new CriticSubstitutionFactory()
        factories << new CriticCommentFactory()
        factories << new CriticHighlightFactory()
    }
}
