package org.supermmx.asciidog.critic.builder

import org.supermmx.asciidog.critic.CriticNode

import org.supermmx.asciidog.builder.factory.InlineContainerFactory

abstract class AbstractCriticNodeFactory extends InlineContainerFactory {
    CriticNode.CriticType criticType

    @Override
    def newInstance(FactoryBuilderSupport builder, name, value, Map attributes) {
        CriticNode node = new CriticNode(criticType: criticType)

        return node
    }

}

