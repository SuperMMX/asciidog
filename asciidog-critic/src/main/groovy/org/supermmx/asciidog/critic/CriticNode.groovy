package org.supermmx.asciidog.critic

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.InlineContainerNode

class CriticNode extends InlineContainerNode {
    static final Node.Type CRITIC_NODE_TYPE =
        new Node.Type(parent: Node.Type.INLINE, name: 'critic')

    static enum CriticType {
        ADDITION,
        DELETION,
        COMMENT,
        HIGHLIGHT,
        SUBSTITUTION
    }

    CriticType criticType

    CriticNode() {
        type = CRITIC_NODE_TYPE
    }
}
